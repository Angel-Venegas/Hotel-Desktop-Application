package entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Scanner;


public class FileSystem {
    private File systemFile;
    private Database database;


    public FileSystem() {

    }


    public FileSystem(Database database) {
        this.database = database;

        String currentDirectory = FileSystem.class.getResource("").getPath();
        systemFile = new File(currentDirectory,"systemFile.txt");

        try {
            if (systemFile.createNewFile())
                System.out.println("File Created Successfully");
            else
                System.out.println("systemFile.txt Opened Successfully");
        } catch (IOException e) { System.out.println("Error: " +  e); }

        if (systemFile.exists() && systemFile.length() == 0) {
            if (database.initializeTables()) { // Meaning tables do not exist so tables were now put in the database
                database.exportDataToFile(systemFile); // Writes already typed data to a text file
                parseIntoDatabase(); // Parse the text file information into the database
            }
            else { // The tables existed
                database.parseIntoTextFile(systemFile, 1); // Writes data from the database into the text file
            }
        }
        else if (database.initializeTables()) { // The systemFile exists and is not empty and the tables do not exist in the database
            parseIntoDatabase(); // Parse the text file information into the database
        }
    }


    public void updateFile(String blockName, String action, String[] updateInfo) { // Index Based Rooms (Not Actual Numbered)
        boolean blockFound = false;
        boolean userRegistered = false;

        File tempFile = new File("tempFile.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(systemFile));
             FileWriter fileWriter = new FileWriter(tempFile)) {
             String currLine;

             while ((currLine = reader.readLine()) != null) {
                 if (currLine.equals(blockName) && !blockFound) { // Found Desired Block In File
                     blockFound = true;
                     fileWriter.write(currLine + "\n");
                     continue;
                 } else if (!blockFound) { // Continue Searching Through The File Since We Have Not Found The Block We Want
                     fileWriter.write(currLine + "\n"); // Write The Current Line (Which Is Before The Found Block)
                     continue;
                 } else if (currLine.equals("***") && blockFound) { // End Of The Block Of Current Block
                     if (action.equals("register user")) { // User was registered And Write It One Line That Used To Be ***
                         fileWriter.write(updateInfo[0] + "--" + updateInfo[1] + "--" + updateInfo[2] + "--" + updateInfo[3]
                                + "--" + updateInfo[4] + "--" + updateInfo[5] + "\n" + currLine);

                         database.insertOrUpdateGuestInformation(Integer.parseInt(updateInfo[0]), updateInfo[1], updateInfo[2], updateInfo[3], updateInfo[4], updateInfo[5], "none",  "none",  "none",  "none");
                         userRegistered = true;
                     }
                     else if (action.equals("guest registered")) { // User was registered And Write It On The Line That Used To Be *** (Recursive Step)
                         fileWriter.write(updateInfo[0] + currLine);
                     }
                     else if (action.equals("register hotel")) { // Write ***
                         fileWriter.write(currLine);
                     }
                     else if (action.equals("cancel room")) { // Write ***
                         fileWriter.write(currLine);
                     }
                     else if (action.equals("update user")) { // Write ***
                         fileWriter.write(currLine);
                     }
                     else if (action.equals("room report")) { // Write ***
                         fileWriter.write(updateInfo[0] + "\n" + currLine);
                         database.insertRoomReview(updateInfo[0].replace("---", ""));
                     }

                     while ((currLine = reader.readLine()) != null) { // Keep Writing Everything After The Found Block
                         fileWriter.write("\n" + currLine);
                     }

                     break;
                 }

                 // The Block Was Found, We Are Inside That Block Now

                 String[] lineSplit = currLine.split("--");


                 if (blockName.equals("***Hotel Room Information:") && action.equals("register hotel") && lineSplit[0].equals(updateInfo[0])) { // Register Room: Matches User ID On A Current Line
                     String[] guestInfo = retrieveLineInfo("***Guest Information:", updateInfo[0]);

                     if (lineSplit[1].equals("none")) { // First Time Registering To Replace "none"
                         fileWriter.write(lineSplit[0] + "--" + updateInfo[1] + "--" + updateInfo[2] + "--" + updateInfo[3] + "--" + updateInfo[4] + "\n");

                         database.insertOrUpdateGuestInformation(Integer.parseInt(guestInfo[0]), guestInfo[1], guestInfo[2], guestInfo[3], guestInfo[4], guestInfo[5], updateInfo[1],  Integer.toString(Integer.parseInt(updateInfo[2]) + 1),  updateInfo[3],  updateInfo[4]);
                     }
                     else { // Add More Hotel Rooms To Guest
                         fileWriter.write(lineSplit[0] + "--" + lineSplit[1] + "," + updateInfo[1] + "--" + lineSplit[2] + "," + updateInfo[2] + "--" + lineSplit[3] + "," + updateInfo[3] + "--" + lineSplit[4] + "," + updateInfo[4] + "\n");

                         database.insertOrUpdateGuestInformation(Integer.parseInt(guestInfo[0]), guestInfo[1], guestInfo[2], guestInfo[3], guestInfo[4], guestInfo[5], lineSplit[1] + "," + updateInfo[1],  updatedRoomsLine(lineSplit[2].split(","), 1) + "," + Integer.toString(Integer.parseInt(updateInfo[2]) + 1),  lineSplit[3] + "," + updateInfo[3],  lineSplit[4] + "," + updateInfo[4]);
                     }
                     continue; // To Avoid Writing The Current Line Again
                 }

                 if (blockName.equals("***Hotel Room Information:") && action.equals("cancel room") && lineSplit[0].equals(updateInfo[0])) { // Cancel Room: Matches User ID On A Current Line
                     String removedFloor = "", removedRoom = "", removedGuests = "", removedPayed= "";
                     int pairLength = lineSplit[1].split(",").length;

                     for (int i = 0; i < pairLength; ++i) { // Looping Each Pair Of Corresponding Floors And Rooms
                         String floor = lineSplit[1].split(",")[i];
                         String room = lineSplit[2].split(",")[i];
                         String guests = lineSplit[3].split(",")[i];
                         String payed = lineSplit[4].split(",")[i];

                         if (floor.equals(updateInfo[1]) && room.equals(updateInfo[2]))
                             continue;

                         removedFloor += floor;
                         removedRoom += room;
                         removedGuests += guests;
                         removedPayed += payed;

                         if (i < pairLength - 1 && pairLength != 2) { // Handles The Case Where 2 Pairs Left And 1st Pair Is Target
                             removedFloor += ",";
                             removedRoom += ",";
                             removedGuests += ",";
                             removedPayed += ",";
                         }
                     }

                     String[] guestInfo = retrieveLineInfo("***Guest Information:" , updateInfo[0]);

                     if (removedFloor.endsWith(",")) { // The Case Where They End With A Comma
                         removedFloor = removedFloor.substring(0, removedFloor.length() - 1);
                         removedRoom = removedRoom.substring(0, removedRoom.length() - 1);
                         removedGuests = removedGuests.substring(0, removedGuests.length() - 1);
                         removedPayed = removedPayed.substring(0, removedPayed.length() - 1);
                     }

                     if (removedFloor.equals("")) { // The Case Where Everything Was Removed
                         fileWriter.write(lineSplit[0] + "--none--none--none--none\n");

                         database.insertOrUpdateGuestInformation(Integer.parseInt(guestInfo[0]), guestInfo[1], guestInfo[2], guestInfo[3], guestInfo[4], guestInfo[5], "none",  "none",  "none",  "none");
                         continue; // To Avoid Writing The Current Line Again
                     }

                     fileWriter.write(lineSplit[0] + "--" + removedFloor + "--" + removedRoom + "--" + removedGuests + "--" + removedPayed + "\n");
                     database.insertOrUpdateGuestInformation(Integer.parseInt(guestInfo[0]), guestInfo[1], guestInfo[2], guestInfo[3], guestInfo[4], guestInfo[5], removedFloor,  updatedRoomsLine(removedRoom.split(","), 1),  removedGuests,  removedPayed);
                     continue; // To Avoid Writing The Current Line Again
                 }

                 if (blockName.equals("***Guest Information:") && action.equals("update user") && lineSplit[0].equals(updateInfo[0])) { // Register Room: Matches User ID On A Current Line
                     fileWriter.write(lineSplit[0] + "--" + updateInfo[1] + "--" + updateInfo[2] + "--" + updateInfo[3] + "--" + updateInfo[4] + "--" + updateInfo[5] + "\n");
                     String[] hotelInfo = retrieveLineInfo("***Hotel Room Information:" , updateInfo[0]);

                     database.insertOrUpdateGuestInformation(Integer.parseInt(updateInfo[0]), updateInfo[1], updateInfo[2], updateInfo[3], updateInfo[4], updateInfo[5], hotelInfo[1],  updatedRoomsLine(hotelInfo[2].split(","), 1),  hotelInfo[3],  hotelInfo[4]);
                     continue; // To Avoid Writing The Current Line Again
                 }

                 fileWriter.write(currLine + "\n"); // Ensures That Everything In The Block Gets Written
             }

        } catch (IOException e) {
            e.printStackTrace();
        }


        if (systemFile.delete()) {
            System.out.println("File deleted successfully.");
            tempFile.renameTo(systemFile);
        } else {
            System.out.println("Failed to delete the file.");
        }

        if (userRegistered) // Extra Step For When A Guest Registers
            updateFile("***Hotel Room Information:", "guest registered", new String[] {updateInfo[0] + "--none--none--none--none\n"});
    }

    private String[] retrieveLineInfo(String blockName, String guestID) {
        ArrayList<String> guestInfo = returnBlock(blockName);

        for (String line : guestInfo) {
            String[] info = line.split("--");

            if (info[0].equals(guestID))
                return info;
        }

        return null;
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

    public void readFile() {
        try {
            BufferedReader characterReader = new BufferedReader(new FileReader(systemFile.getName()));
            String fileLine;

            while ( (fileLine = characterReader.readLine()) != null ) {
                System.out.println(fileLine);
            }
        } catch (IOException e) { System.out.println("Error: " +  e); }
    }

    public ArrayList<String> returnBlock(String blockName) {
        if (blockName.equals("***Guest Information:") || blockName.equals("***Hotel Room Information:") || blockName.equals("***Hotel Room Reports:")) {

        }
        else
            return null;

        ArrayList<String> blockInfo = new ArrayList<>();
        boolean blockFound = false;

        try (Scanner fileScanner = new Scanner(systemFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                if (line == null || line.length() < 3) { // So That We Can Store Something Large Enough In Line For Substring To Not Get An Error
                    continue;
                }

                if (line.startsWith("***") && line.equals(blockName) && !blockFound) { // Found lineIndicator Line And Skip It
                    blockFound = true; // Set blockFound To True To Access Content After The Line And Prevent The Continued Loop
                    continue;
                } else if (!blockFound) { // Continue Searching Through The File Since We Have Not Found The Block We Want
                    continue;
                }

                if (line.substring(0,3).equals("***") && blockFound) { // The End Of The Block Was Reached So Exit The Loop
                    break;
                }

                // The Block Was Found, We Are Inside That Block Now

                blockInfo.add(line); // Add The Inside Lines Of The Block Only
            }
        } catch (IOException e) { System.out.println("Error: " + e); }

        return blockInfo;
    }

    public File getFile() {
        return systemFile;
    }


    public void parseIntoDatabase() { // Parses The File Information Into The Database
        ArrayList<String> guestInfo = returnBlock("***Guest Information:");
        ArrayList<String> hotelInfo = returnBlock("***Hotel Room Information:");
        ArrayList<String> reviewInfo = returnBlock("***Hotel Room Reports:");

        for (int i = 0; i < guestInfo.size(); ++i) {
            String[] info1 = guestInfo.get(i).split("--");
            String[] info2 = hotelInfo.get(i).split("--");

            String[] rooms = info2[2].split(",");

            database.insertOrUpdateGuestInformation(Integer.parseInt(info1[0]), info1[1], info1[2], info1[3], info1[4], info1[5], info2[1], updatedRoomsLine(rooms, 1), info2[3], info2[4]);
        }

        StringBuilder eachReview = new StringBuilder();

        for (String line : reviewInfo) {
            if (line.equals("---")) {
                database.insertRoomReview(eachReview.toString());
                eachReview.setLength(0);
                continue;
            }

            eachReview.append(line  + "\n");
        }
    }
}