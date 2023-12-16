package entity;

import java.util.ArrayList;


public class Manager extends ManagementSystem {
    private ManagementSystem managementSystem;
    private ArrayList<String> hotelRoomReviews;
    public String username;
    public String password;


    public Manager() {
        hotelRoomReviews = new ArrayList<>();
        username = "m";
        password = "p";
    }

    public void assignManagementSystem(ManagementSystem ms) {
        managementSystem = ms;
        ArrayList<String> roomReports = managementSystem.fileSystem.returnBlock("***Hotel Room Reports:");

        StringBuilder eachReview = new StringBuilder();

        for (String line : roomReports) {
            if (line.equals("---")) {
                hotelRoomReviews.add(eachReview.toString().substring(0, eachReview.toString().length() - 1));
                eachReview.setLength(0);
                continue;
            }

            eachReview.append(line  + "\n");
        }
    }

    public void addReview(String report) {
        hotelRoomReviews.add(report);
    }

    public ArrayList<String> getReviews() {
        return hotelRoomReviews;
    }
}
