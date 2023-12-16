package control;

import entity.FileSystem;
import entity.Guest;
import entity.ManagementSystem;


import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;

/*
superDeluxeRooms
deluxeRooms
quadRooms
tripleRooms
doubleRooms
singleRooms
*/


public class HotelRooms {
    private FileSystem fileSystem;
    private HashMap<Integer, String> floorMap;
    private ArrayList<ArrayList<String>> hotelRoomTypes; // Store guestID to a room otherwise mark it as Open
    private HashMap<Integer, Guest> guestsMap;


    public HotelRooms() {

    }

    public HotelRooms(FileSystem fileSystem) {
        this.fileSystem = fileSystem;

        floorMap = new HashMap<Integer, String>();

        floorMap.put(1, "Single Room");
        floorMap.put(2, "Double Room");
        floorMap.put(3, "Triple Room");
        floorMap.put(4, "Quad Room");
        floorMap.put(5, "Deluxe Room");
        floorMap.put(6, "Super Deluxe Room");


        hotelRoomTypes = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            hotelRoomTypes.add(new ArrayList<>(Collections.nCopies(10, "Open")));
        }

        guestsMap = new HashMap<>();

        storeHotelInfo();
    }


    private void storeHotelInfo() { // Reads from systemFile and associates a Guest object with an ID and stores Guest instances in the hotelRooms
        ArrayList<String> guestBlock = fileSystem.returnBlock("***Guest Information:");
        ArrayList<String> hotelBlock = fileSystem.returnBlock("***Hotel Room Information:");

        for (int i = 0; i < guestBlock.size(); ++i) {
            String[] lineSplit1 = guestBlock.get(i).split("--");
            String[] lineSplit2 = hotelBlock.get(i).split("--");

            guestsMap.put(Integer.valueOf(lineSplit1[0]), new Guest(Integer.parseInt(lineSplit1[0]), lineSplit1[1], lineSplit1[2], lineSplit1[3], lineSplit1[4], lineSplit1[5],
                    lineSplit2[1], lineSplit2[2], lineSplit2[3], lineSplit2[4]));


            String[] roomTypes = lineSplit2[1].split(",");
            String[] roomNumbers = lineSplit2[2].split(",");

            for (int j = 0; j < roomTypes.length; ++j) {
                switch (roomTypes[j]) { // Sets A GuestID to A Floor With Specified Room
                    case "Super Deluxe Room" -> hotelRoomTypes.get(5).set(Integer.parseInt(roomNumbers[j]), lineSplit2[0]); // (Room Number, Guest ID)
                    case "Deluxe Room" -> hotelRoomTypes.get(4).set(Integer.parseInt(roomNumbers[j]), lineSplit2[0]);
                    case "Quad Room" -> hotelRoomTypes.get(3).set(Integer.parseInt(roomNumbers[j]), lineSplit2[0]);
                    case "Triple Room" -> hotelRoomTypes.get(2).set(Integer.parseInt(roomNumbers[j]), lineSplit2[0]);
                    case "Double Room" -> hotelRoomTypes.get(1).set(Integer.parseInt(roomNumbers[j]), lineSplit2[0]);
                    case "Single Room" -> hotelRoomTypes.get(0).set(Integer.parseInt(roomNumbers[j]), lineSplit2[0]);
                }
            }
        }
    }


    public void updateHotelInfo(Guest guest) {
        guestsMap.put(guest.getGuestID(), guest);
    }

    public ArrayList<ArrayList<String>> getRoomStatus() { // Returns A Copy Of The Hotel Rooms So The Actual Rooms Don't Accidentally Get Modified
        ArrayList<ArrayList<String>> openRooms = new ArrayList<>();

        for (int i = 0; i < hotelRoomTypes.size(); ++i) {
            ArrayList<String> rooms = new ArrayList<>();
            for (int j = 0; j < hotelRoomTypes.get(i).size(); ++j) {
                String roomStatus = hotelRoomTypes.get(i).get(j);

                if (roomStatus.equals("Open"))
                    rooms.add("Open");
                else
                    rooms.add(hotelRoomTypes.get(i).get(j));
            }
            openRooms.add(rooms);
        }

        return openRooms;
    }

    public boolean roomAvailable(int floor, int room) {
        if (hotelRoomTypes.get(floor).get(room).equals("Open"))
            return true;

        return false;
    }

    public void registerRoom(int guestID, int floor, int room, int totalGuests, int amountPaid) { // (floor - 1), (room - 1)
        if (roomAvailable(floor, room)) {
            hotelRoomTypes.get(floor).set(room, Integer.toString(guestID)); // Sets ID To A Room Given A Floor
            fileSystem.updateFile("***Hotel Room Information:", "register hotel", new String[]{Integer.toString(guestID), floorMap.get(floor + 1), Integer.toString(room), Integer.toString(totalGuests), Integer.toString(amountPaid)});
        }
     }

    public void cancelRoom(int guestID, int floor, int room) { // (floor - 1), (room - 1)
        hotelRoomTypes.get(floor).set(room, "Open"); // Sets "Open" To A Room Given A Floor
        fileSystem.updateFile("***Hotel Room Information:", "cancel room", new String[]{Integer.toString(guestID), floorMap.get(floor + 1), Integer.toString(room)});
    }

    public String getFloor(int floor) {
        return floorMap.get(floor);
    }

    public HashMap<Integer, Guest> getGuestMap() {
        return guestsMap;
    }

    public void printHotelStatus() {
        System.out.println();
        for (int i = 0; i < hotelRoomTypes.size(); i++) {
            ArrayList<String> roomType = hotelRoomTypes.get(i);
            System.out.println("Room type " + (i + 1) + ": " + roomType);
        }
    }



    public class RoomInfo {
        private int floor;
        private int roomNumber;

        public RoomInfo(int floor, int roomNumber) {
            this.floor = floor;
            this.roomNumber = roomNumber;
        }

        public int getFloor() {
            return floor;
        }

        public int getRoomNumber() {
            return roomNumber;
        }

        @Override
        public String toString() {
            return "FLOOR " + floor + ": Room " + roomNumber;
        }
    }
}