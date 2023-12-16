package entity;


public class Guest {
    private int guestID;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String userPassword;
    private String roomType;
    private String roomChosen;
    private String totalGuests;
    private String amountPaid;
    private int totalPaid = 0;
    private boolean roomStatus;


    public Guest() {

    }

    public Guest(int guestID, String email, String firstName, String lastName, String username, String userPassword, String roomType, String roomChosen, String totalGuests, String amountPaid) {
        this.guestID = guestID;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.userPassword = userPassword;
        this.roomType = roomType;
        this.roomChosen = roomChosen;
        this.totalGuests = totalGuests;
        this.amountPaid = amountPaid;

        calculateTotalPaid();
    }
    //1122--bob1220@gmail.com--Bob--Bobertson--Bob1220!--Bob0221?--superDeluxeRooms--1--10--8000


    private void calculateTotalPaid() {
        String[] paymentStrings = amountPaid.split(",");

        if (!paymentStrings[0].equals("none"))
            for (int i = 0; i < paymentStrings.length; i++)
                totalPaid += Integer.parseInt(paymentStrings[i]);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setRoomChosen(String roomChosen) {
        this.roomChosen = roomChosen;
    }

    public void setTotalGuests(String totalGuests) {
        this.totalGuests = totalGuests;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public void setRoomStatus(boolean roomStatus) {
        this.roomStatus = roomStatus;
    }

    public void setTotalPaid(int totalPaid) {
        this.totalPaid = totalPaid;
    }

    //----------------------------------------------------------------------------//

    public int getGuestID() {
        return guestID;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomChosen() {
        return roomChosen;
    }

    public String getTotalGuests() {
        return totalGuests;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public boolean getRoomStatus() {
        return roomStatus;
    }

    public int getTotalPaid() {
        return totalPaid;
    }

}
