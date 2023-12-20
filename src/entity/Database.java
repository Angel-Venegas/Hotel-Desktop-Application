package entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    private Connection connection;


    public Database() {
        try {
            // Establish a database connection
            String url = "jdbc:mysql://localhost:3306/hotel_db";
            String username = "HotelGuest";
            String password = "HotelGuestLogin123";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Error initializing database: ", e);
        }
    }


    public void insertOrUpdateGuestInformation(
            int guestID, String email, String firstName, String lastName,
            String username, String userPassword, String roomType,
            String roomChosen, String totalGuests, String amountPaid) {
        try {
            // Define the SQL query to check for an existing record
            String checkExistingSql = "SELECT guestID FROM GuestInformation WHERE guestID = ?";

            // Create a PreparedStatement to check for an existing record
            PreparedStatement checkExistingStatement = connection.prepareStatement(checkExistingSql);
            checkExistingStatement.setInt(1, guestID);

            // Execute the query to check for an existing record
            ResultSet resultSet = checkExistingStatement.executeQuery();

            if (resultSet.next()) {
                // Record with the same guestID exists, update the existing record
                String updateSql = "UPDATE GuestInformation SET email=?, firstName=?, lastName=?, username=?, userPassword=?, roomType=?, roomChosen=?, totalGuests=?, amountPaid=? WHERE guestID=?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);

                // Set the parameters for the update
                updateStatement.setString(1, email);
                updateStatement.setString(2, firstName);
                updateStatement.setString(3, lastName);
                updateStatement.setString(4, username);
                updateStatement.setString(5, userPassword);
                updateStatement.setString(6, roomType);
                updateStatement.setString(7, roomChosen);
                updateStatement.setString(8, totalGuests);
                updateStatement.setString(9, amountPaid);
                updateStatement.setInt(10, guestID);

                // Execute the update query
                updateStatement.executeUpdate();

                // Close the PreparedStatement for the update
                updateStatement.close();
            } else {
                // No record with the same guestID found, insert a new record
                String insertSql = "INSERT INTO GuestInformation (guestID, email, firstName, lastName, username, userPassword, roomType, roomChosen, totalGuests, amountPaid) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement insertStatement = connection.prepareStatement(insertSql);

                // Set the parameters for the insert
                insertStatement.setInt(1, guestID);
                insertStatement.setString(2, email);
                insertStatement.setString(3, firstName);
                insertStatement.setString(4, lastName);
                insertStatement.setString(5, username);
                insertStatement.setString(6, userPassword);
                insertStatement.setString(7, roomType);
                insertStatement.setString(8, roomChosen);
                insertStatement.setString(9, totalGuests);
                insertStatement.setString(10, amountPaid);

                // Execute the insert query
                insertStatement.executeUpdate();

                // Close the PreparedStatement for the insert
                insertStatement.close();
            }

            // Close the ResultSet and PreparedStatement for checking existing record
            resultSet.close();
            checkExistingStatement.close();
        } catch (SQLException e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Error inserting guest information: ", e);
        }
    }


    public void insertRoomReview(String reviewText) {
        try {
            // Define the SQL query to insert data into the RoomReviews table
            String sql = "INSERT INTO RoomReviews (reviewDescription) VALUES (?)";

            // Create a PreparedStatement to safely execute the query
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, reviewText);

            // Execute the SQL query to insert the review
            preparedStatement.executeUpdate();

            // Close the PreparedStatement
            preparedStatement.close();
        } catch (SQLException e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Error inserting room review: ", e);
        }
    }


    public int parseIntoTextFile(File systemFile, int i) {
        String tableName, blockTitle;

        if (i == 1) {
            tableName = "GuestInformation";
            blockTitle = "***Guest Information:";
        } else if (i == 2) {
            tableName = "GuestInformation";
            blockTitle = "***Hotel Room Information:";
        } else if (i == 3) {
            tableName = "RoomReviews";
            blockTitle = "***Hotel Room Reports:";
        } else {
            return -1; // Invalid value for i
        }

        int rowsRead = 0; // Number of rows read from the database

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
             FileWriter fileWriter = new FileWriter(systemFile, true)) {

            fileWriter.write(blockTitle + "\n");

            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            while (resultSet.next()) {
                for (int j = 1; j <= numberOfColumns; j++) {
                    if (blockTitle.equals("***Guest Information:") && (j > 6))
                        continue;
                    else if (blockTitle.equals("***Hotel Room Information:") && (j > 1 && j < 7))
                        continue;
                    else if (blockTitle.equals("***Hotel Room Reports:") && (j == 1))
                        continue;

                    String columnValue = resultSet.getString(j);

                    if (blockTitle.equals("***Hotel Room Reports:")) {
                        fileWriter.write(columnValue + "---");
                        if (!resultSet.isLast())
                            fileWriter.write("\n");
                    } else { // ***Guest Information: and ***Hotel Room Information:
                        if (j == 8 && !columnValue.equals("none"))
                            fileWriter.write(updatedRoomsLine(columnValue.split(",")));
                        else
                            fileWriter.write(columnValue);
                    }

                    if (j < numberOfColumns) {
                        if (blockTitle.equals("***Hotel Room Reports:"))
                            fileWriter.write("\n");
                        else {
                            if (j < 6 && blockTitle.equals("***Guest Information:"))
                                fileWriter.write("--"); // Add tab as a separator between columns
                            else if (blockTitle.equals("***Hotel Room Information:"))
                                fileWriter.write("--"); // Add tab as a separator between columns
                        }
                    }
                }
                if (!blockTitle.equals("***Hotel Room Reports:") && !resultSet.isLast()) // ***Guest Information: and ***Hotel Room Information:
                    fileWriter.write("\n");

                rowsRead++; // Increment the count of rows read
            }

            fileWriter.write("\n***\n\n");

        } catch (SQLException | IOException e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Error parsing into text file: ", e);
        }

        int recursiveRows = parseIntoTextFile(systemFile, ++i); // Recursive step to parse each table into the text file

        return rowsRead + recursiveRows; // Return the total number of rows read
    }


    private String updatedRoomsLine(String[] line) {
        StringBuilder roomsLine = new StringBuilder();

        for (int i = 0; i < line.length; i++) {
            if (!line[i].equals("none")) {
                roomsLine.append(Integer.parseInt(line[i]) - 1);

                if (i < line.length - 1)
                    roomsLine.append(",");
            }
            else
                roomsLine.append(line[i]);
        }

        return roomsLine.toString();
    }


    public void exportDataToFile(File systemFile) {
        try (FileWriter writer = new FileWriter(systemFile)) {
            writeSampleGuestInformation(writer);
            writeSampleHotelRoomInformation(writer);
            writeSampleHotelRoomReports(writer);
        } catch (IOException e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Error exporting data to file: ", e);
        }
    }

    private void writeSampleGuestInformation(FileWriter writer) throws IOException {
        writer.write("***Guest Information:\n");
        writer.write("1287--guest17@example.com--First17--Last17--username17--password17\n");
        writer.write("1010--guest10@example.com--First10--Last10--username10--password10\n");
        writer.write("1232--guest0@example.com--First0--Last0--username0--password0\n");
        writer.write("1001--guest1@example.com--First1--Last1--username1--password1\n");
        writer.write("6328--guest15@example.com--First15--Last15--username15--password15\n");
        writer.write("1703--guest3@example.com--First3--Last3--username3--password3\n");
        writer.write("4593--guest19@example.com--First19--Last19--username19--password19\n");
        writer.write("1804--guest4@example.com--First4--Last4--username4--password4\n");
        writer.write("1806--guest6@example.com--First6--Last6--username6--password6\n");
        writer.write("1077--guest7@example.com--First7--Last7--username7--password7\n");
        writer.write("2948--guest18@example.com--First18--Last18--username18--password18\n");
        writer.write("9999--guest8@example.com--First8--Last8--username8--password8\n");
        writer.write("1111--guest9@example.com--First9--Last9--username9--password9\n");
        writer.write("2222--guest12@example.com--First12--Last12--username12--password12\n");
        writer.write("3333--guest11@example.com--First11--Last11--username11--password11\n");
        writer.write("2848--guest18@example.com--First18--Last18--username18--password18\n");
        writer.write("7783--guest14@example.com--First14--Last14--username14--password14\n");
        writer.write("9430--guest16@example.com--First16--Last16--username16--password16\n");
        writer.write("1070--guest2@example.com--First2--Last2--username2--password2\n");
        writer.write("5555--guest13@example.com--First13--Last13--username13--password13\n");
        writer.write("6074--a--a--a--a--a\n");
        writer.write("9207--b--b--b--b--b\n");
        writer.write("4900--aa--aa--aa--aa--aa\n");
        writer.write("9128--j--j--j--j--j\n");
        writer.write("2827--h--h--h--h--h\n");
        writer.write("2904--glimon0009@gmail.com--Gabriel--Limon--limongoat--limon21\n***\n");
    }

    private void writeSampleHotelRoomInformation(FileWriter writer) throws IOException {
        writer.write("\n***Hotel Room Information:\n");
        writer.write("1287--Quad Room--9--4--4000\n");
        writer.write("1010--Triple Room--5--3--3000\n");
        writer.write("1232--Triple Room--4--3--3000\n");
        writer.write("1001--Quad Room--4--4--4000\n");
        writer.write("6328--Single Room--7--1--1000\n");
        writer.write("1703--Single Room--9--1--1000\n");
        writer.write("4593--Super Deluxe Room--7--5--9000\n");
        writer.write("1804--Super Deluxe Room--3--6--9000\n");
        writer.write("1806--Super Deluxe Room--0--9--9000\n");
        writer.write("1077--Single Room--2--1--1000\n");
        writer.write("2948--Triple Room--1--3--3000\n");
        writer.write("9999--Deluxe Room--8--6--6000\n");
        writer.write("1111--Quad Room--0--4--4000\n");
        writer.write("2222--Triple Room--0--3--3000\n");
        writer.write("3333--Super Deluxe Room--6--7--9000\n");
        writer.write("2848--Triple Room--2--3--3000\n");
        writer.write("7783--Single Room--0--1--1000\n");
        writer.write("9430--Single Room--8--1--1000\n");
        writer.write("1070--Quad Room--6--4--4000\n");
        writer.write("5555--Double Room--5--2--2000\n");
        writer.write("6074--Double Room,Double Room,Quad Room--0,7,8--2,1,1--2000,2000,4000\n");
        writer.write("9207--none--none--none--none\n");
        writer.write("4900--none--none--none--none\n");
        writer.write("9128--none--none--none--none\n");
        writer.write("2827--Single Room,Single Room--3,6--1,1--1000,1000\n");
        writer.write("2904--none--none--none--none\n***\n");
    }

    private void writeSampleHotelRoomReports(FileWriter writer) throws IOException {
        writer.write("\n***Hotel Room Reports:\n");
        writer.write("★★★★★\nSingle Room:\n\tReally great a comfy room.\n---\n");
        writer.write("★★★★★\nSuper Deluxe Room:\n\tThere is lots of room for many people in this hotel\n---\n");
        writer.write("★★★★★\nSuper Deluxe Room:\n\tVery elegant looking hotel!!\n---\n");
        writer.write("★★★★☆\nDeluxe Room:\n\talright\n---\n");
        writer.write("★★★☆☆\nSingle Room:\n\tCould be better.\n\tHad better experiences elsewhere.\n---\n");
        writer.write("★★★★★\nDouble Room:\n\tVery nice\n---\n");
        writer.write("★☆☆☆☆\nQuad Room:\n\tThe manager at the hotel was rude to me for no reason.\n---\n");
        writer.write("★★★★★\nDouble Room:\n\tNoice!!!\n---\n***");
    }

    public boolean initializeTables() {
        try (Statement statement = connection.createStatement()) {

            // Check if GuestInformation table exists
            ResultSet resultSet = connection.getMetaData().getTables(null, null, "GuestInformation", null);
            if (resultSet.next()) {
                // GuestInformation table exists
                resultSet.close();

                // Check if RoomReviews table exists
                resultSet = connection.getMetaData().getTables(null, null, "RoomReviews", null);
                if (resultSet.next()) {
                    // RoomReviews table exists
                    resultSet.close();
                    System.out.println("Tables already exist.");
                    return false;
                }

                // Create RoomReviews table
                String createRoomReviewsTable = "CREATE TABLE RoomReviews ("
                        + "reviewID INT AUTO_INCREMENT PRIMARY KEY,"
                        + "reviewDescription TEXT)";
                statement.executeUpdate(createRoomReviewsTable);
                System.out.println("RoomReviews table created successfully.");
            } else {
                // Create GuestInformation table
                String createGuestInformationTable = "CREATE TABLE GuestInformation ("
                        + "guestID INT AUTO_INCREMENT PRIMARY KEY,"
                        + "email TEXT,"
                        + "firstName TEXT,"
                        + "lastName TEXT,"
                        + "username TEXT,"
                        + "userPassword TEXT,"
                        + "roomType TEXT,"
                        + "roomChosen TEXT,"
                        + "totalGuests TEXT,"
                        + "amountPaid TEXT)";
                statement.executeUpdate(createGuestInformationTable);
                System.out.println("GuestInformation table created successfully.");

                // Create RoomReviews table
                String createRoomReviewsTable = "CREATE TABLE RoomReviews ("
                        + "reviewID INT AUTO_INCREMENT PRIMARY KEY,"
                        + "reviewDescription TEXT)";
                statement.executeUpdate(createRoomReviewsTable);
                System.out.println("RoomReviews table created successfully.");
            }

            return true;

        } catch (SQLException e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Error initializing tables: ", e);
            return false;
        }
    }

    /*
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */
}


// Login Details:
/*
CREATE USER 'HotelGuest'@'localhost' IDENTIFIED BY 'HotelGuestLogin123';
GRANT ALL PRIVILEGES ON `hotel_db`.* TO 'HotelGuest'@'localhost';
FLUSH PRIVILEGES;
*/