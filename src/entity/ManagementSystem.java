package entity;


import control.HotelRooms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class ManagementSystem extends HotelRooms {
    protected HotelRooms hotelRooms;
    protected FileSystem fileSystem;
    private Set<Integer> generatedIDs = new HashSet<>();
    protected ArrayList<Guest> guestList;


    public ManagementSystem() {

    }

    public ManagementSystem(FileSystem fileSystem, HotelRooms hotelRooms) {
        this.fileSystem = fileSystem;
        this.hotelRooms = hotelRooms;
        guestList = new ArrayList<>();

        // Add guests from the HotelRooms HashMap to the guestList
        HashMap<Integer,Guest> guestMap = hotelRooms.getGuestMap();

        for (HashMap.Entry<Integer, Guest> entry : guestMap.entrySet())
            guestList.add(entry.getValue());

        // Store The Existing IDs From The systemFile
        storeIDS(null);
    }


    private void storeIDS(Guest guest) { // Store All Guest IDs From The File Into The HashSet Otherwise Add It
        if (guest == null) {
            ArrayList<String> blockInfo = fileSystem.returnBlock("***Guest Information:");

            for (String line : blockInfo) {
                String[] lineSplit = line.split("--");
                generatedIDs.add(Integer.parseInt(lineSplit[0]));
            }

            return;
        }

        generatedIDs.add(guest.getGuestID());
    }

    public void updateSystemInfo(Guest guest) {
        if (guest != null)
            guestList.add(guest);

        storeIDS(guest);
    }

    public int generateID() { // Ensures No Duplicate Ids Get Generated
        Random rand = new Random();
        int id;

        do {
            id = rand.nextInt(9000) + 1000; // Generates A Random ID Between 1000 - 9999
        } while (generatedIDs.contains(id));

        generatedIDs.add(id);

        return id;
    }


    public Boolean confirmUsername(String username) { // Confirms A Guest's Username
        for (Guest guest : guestList) {
            if (guest.getUsername().equals(username))
                return true;
        }

        return false;
    }


    public Boolean confirmPassword(String username, String password) { // Confirms A Guest's Password If Associated With A Username
        for (Guest guest : guestList) {
            if (guest.getUsername().equals(username) && guest.getUserPassword().equals(password))
                return true;
        }

        return false;
    }


    public Guest retrieveGuest(String userName, String password) { // Return A Guest Reference If The Username and Password are Associated To It
        for (Guest guest : guestList)
            if ( guest.getUsername().equals(userName) && guest.getUserPassword().equals(password) )
                return guest;

        return null;
    }

}
