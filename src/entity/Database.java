package entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

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
            e.printStackTrace();
        }
    }


    public void insertOrUpdateGuestInformation(
            int guestID, String email, String firstName, String lastName,
            String username, String userPassword, String roomType,
            String roomChosen, String totalGuests, String amountPaid) {
        try {
            // Define the SQL query to check for an existing record
            String checkExistingSql = "SELECT guestID FROM guestinformation WHERE guestID = ?";

            // Create a PreparedStatement to check for an existing record
            PreparedStatement checkExistingStatement = connection.prepareStatement(checkExistingSql);
            checkExistingStatement.setInt(1, guestID);

            // Execute the query to check for an existing record
            ResultSet resultSet = checkExistingStatement.executeQuery();

            if (resultSet.next()) {
                // Record with the same guestID exists, update the existing record
                String updateSql = "UPDATE guestinformation SET email=?, firstName=?, lastName=?, username=?, userPassword=?, roomType=?, roomChosen=?, totalGuests=?, amountPaid=? WHERE guestID=?";
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
                String insertSql = "INSERT INTO guestinformation (guestID, email, firstName, lastName, username, userPassword, roomType, roomChosen, totalGuests, amountPaid) " +
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
            e.printStackTrace();
        }
    }


    public void insertRoomReview(String reviewText) {
        try {
            // Define the SQL query to insert data into the room_reviews table
            String sql = "INSERT INTO room_reviews (reviewText) VALUES (?)";

            // Create a PreparedStatement to safely execute the query
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, reviewText);

            // Execute the SQL query to insert the review
            preparedStatement.executeUpdate();

            // Close the PreparedStatement
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void parseIntoTextFile(File systemFile, int i) {
        String tableName = null, blockTitle = null;

        if (i == 1) {
            tableName = "guestinformation";
            blockTitle = "***Guest Information:";
        }
        else if (i == 2) {
            tableName = "guestinformation";
            blockTitle = "***Hotel Room Information:";
        }
        else if (i == 3) {
            tableName = "room_reviews";
            blockTitle = "***Hotel Room Reports:";
        }
        else
            return;


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
                    }
                    else if (!blockTitle.equals("***Hotel Room Reports:")) { // ***Guest Information: and ***Hotel Room Information:
                        if (j == 8 && !columnValue.equals("none"))
                            fileWriter.write(updatedRoomsLine(columnValue.split(","), -1));
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

            }

            fileWriter.write("\n***\n\n");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        parseIntoTextFile(systemFile, ++i);
    }


    private String updatedRoomsLine(String[] line, int inc) {
        StringBuilder roomsLine = new StringBuilder();

        for (int i = 0; i < line.length; i++) {
            if (!line[i].equals("none")) {
                roomsLine.append(Integer.toString(Integer.parseInt(line[i]) + inc));

                if (i < line.length - 1)
                    roomsLine.append(",");
            }
            else
                roomsLine.append(line[i]);
        }

        return roomsLine.toString();
    }


    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


// Login Details:
/*
CREATE USER 'HotelGuest'@'localhost' IDENTIFIED BY 'HotelGuestLogin123';
GRANT ALL PRIVILEGES ON `hotel_db`.* TO 'HotelGuest'@'localhost';
FLUSH PRIVILEGES;


*/