package boundary.hoteloptionspackage;

import boundary.SetterMethods;
import control.HotelRooms;
import entity.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.ArrayList;


public class ChangeRoomController implements SetterMethods {
    private Guest guest;
    private Manager manager;
    private Database database;
    private FileSystem fileSystem;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    private HotelOptionsController hotelOptionsController;
    @FXML
    private Label userCredentialLabel;
    @FXML
    private ListView openRoomsListView;
    @FXML
    private ListView registeredRoomsListView;
    private ObservableList<String> openRoomCells;
    private ObservableList<String> registeredRoomCells;
    private ArrayList<ArrayList<String>> availableRooms;
    private ArrayList<HotelRooms.RoomInfo> openSwitchableRooms; // Stores Floor And Room Numbers
    private ArrayList<HotelRooms.RoomInfo> registeredSwitchableRooms; // Stores Floor And Room Numbers
    private HotelRooms.RoomInfo selectedOpenRoom;
    private HotelRooms.RoomInfo selectedRegisteredRoom;
    private int switchToFloor = -1, switchToRoom = -1, switchNumPeople = -1;
    private int cancelFloor = -1, cancelRoom = -1;


    @FXML
    private void initialize() {
        openRoomCells = FXCollections.observableArrayList();
        registeredRoomCells = FXCollections.observableArrayList();
        openSwitchableRooms = new ArrayList<>();
        registeredSwitchableRooms = new ArrayList<>();
    }


    public void setGuest(Guest guest) {
        this.guest = guest;
        userCredentialLabel.setText( String.format("ID: %-8d %s", guest.getGuestID(), "Full Name: " + guest.getFirstName() + ", " + guest.getLastName()) );
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void setObjects(Database database, FileSystem fileSystem, HotelRooms hotelRooms, ManagementSystem managementSystem) {
        this.database = database;
        this.fileSystem = fileSystem;
        this.hotelRooms = hotelRooms;
        this.managementSystem = managementSystem;
    }


    @FXML
    private void submitOnAction(ActionEvent actionEvent) throws Exception  {
        if (cancelFloor == -1 || cancelRoom == -1) {
            alertBox("Please Select A Room To Switch From");
            return;
        }
        else if (switchToFloor == -1 || switchToRoom == -1 || switchNumPeople == -1) {
            alertBox("Please Select A Room To Switch To");
            return;
        }


        int amountToPay = 0;

        if (switchToFloor == 1)
            amountToPay = 1000;
        else if (switchToFloor == 2)
            amountToPay = 2000;
        else if (switchToFloor == 3)
            amountToPay = 3000;
        else if (switchToFloor == 4)
            amountToPay = 4000;
        else if (switchToFloor == 5)
            amountToPay = 6000;
        else if (switchToFloor == 6)
            amountToPay = 9000;

        hotelRooms.cancelRoom(guest.getGuestID(), cancelFloor - 1, cancelRoom - 1);
        hotelRooms.registerRoom(guest.getGuestID(), switchToFloor - 1, switchToRoom - 1, switchNumPeople, amountToPay);

        updateRegisteredRooms();
        updateAvailableRooms(-1);

        infoBox("Room Successfully Switched From Floor " + cancelFloor + ", Room " + cancelRoom + "\nTo Floor "
        + switchToFloor + ", Room " + switchToRoom);

        cancelFloor = cancelRoom = switchToFloor = switchToRoom = switchNumPeople = -1;
    }

    @FXML
    private void cancelOnAction(ActionEvent actionEvent) throws Exception  {
        changeScene(actionEvent);
    }

    @FXML
    private void openRoomsCellClicked() {
        int selectedIndex = openRoomsListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0 && selectedIndex < openRoomCells.size()) {
            selectedOpenRoom = openSwitchableRooms.get(selectedIndex);

            if (selectedOpenRoom == null) {

            }
            else {
                switchToFloor = selectedOpenRoom.getFloor();
                switchToRoom = selectedOpenRoom.getRoomNumber();
                return;
            }
        }

        switchToFloor = -1;
        switchToRoom = -1;
    }

    @FXML
    private void registeredRoomsCellClicked() {
        int selectedIndex = registeredRoomsListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0 && selectedIndex < registeredRoomCells.size()) {
            selectedRegisteredRoom = registeredSwitchableRooms.get(selectedIndex);

            if (selectedRegisteredRoom == null) {

            } else {
                ArrayList<String> guestBlock = fileSystem.returnBlock("***Hotel Room Information:");
                String floorNames[];
                String roomNums[];
                String numPeople[];

                for (String guestInfo : guestBlock) {
                    String lineSplit[] = guestInfo.split("--");

                    if (lineSplit[0].equals(Integer.toString(guest.getGuestID()))) {
                        floorNames = lineSplit[1].split(",");
                        roomNums = lineSplit[2].split(",");
                        numPeople = lineSplit[3].split(",");

                        for (int i = 0; i < floorNames.length; ++i) {
                            int floorNum = selectedRegisteredRoom.getFloor();
                            int roomNum = selectedRegisteredRoom.getRoomNumber();
                            String floorName = hotelRooms.getFloor(floorNum);

                            if (floorNames[i].equals(floorName) && roomNums[i].equals(Integer.toString(roomNum - 1))) {
                                if (Integer.parseInt(numPeople[i]) == 1)
                                    updateAvailableRooms(0);
                                else if (Integer.parseInt(numPeople[i]) == 2)
                                    updateAvailableRooms(1);
                                else if (Integer.parseInt(numPeople[i]) == 3)
                                    updateAvailableRooms(2);
                                else if (Integer.parseInt(numPeople[i]) == 4)
                                    updateAvailableRooms(3);
                                else if (Integer.parseInt(numPeople[i]) <= 6)
                                    updateAvailableRooms(4);
                                else if (Integer.parseInt(numPeople[i]) <= 10)
                                    updateAvailableRooms(5);

                                cancelFloor = floorNum;
                                cancelRoom = roomNum;
                                switchNumPeople = Integer.parseInt(numPeople[i]);
                                return;
                            }
                        }
                    }
                }
            }
        }

        cancelFloor = -1;
        cancelRoom = -1;
        switchNumPeople = -1;
    }


    public void updateAvailableRooms(int floorNum) {
        openRoomCells.clear();
        openSwitchableRooms.clear();
        if (floorNum == -1)
            return;
        availableRooms = hotelRooms.getRoomStatus();

        for (int i = floorNum; i < availableRooms.size(); ++i) {
            String floor = "FLOOR " + (i + 1) + ": " + hotelRooms.getFloor(i + 1) + "s\n\n";
            openRoomCells.add(floor);

            ArrayList<String> roomsOnFloor = availableRooms.get(i);
            openSwitchableRooms.add(null);

            for (int j = 0; j < roomsOnFloor.size(); ++j) {
                HotelRooms.RoomInfo roomInfo;

                if (roomsOnFloor.get(j).equals("Open")) {
                    roomInfo = hotelRooms.new RoomInfo(i + 1, j + 1);
                    openSwitchableRooms.add(roomInfo);

                    openRoomCells.add("Room " + roomInfo.getRoomNumber());
                }
            }

            if (i < availableRooms.size() - 1) {
                openRoomCells.add("\n\n");
                openSwitchableRooms.add(null);
            }
        }

        int numNewLinesToAdd = 25 - openRoomCells.size();

        if (numNewLinesToAdd > 0) { // To Fill In The Bottom Gap Of List View
            StringBuilder newLines = new StringBuilder();
            for (int i = 0; i < numNewLinesToAdd; i++)
                newLines.append("\n\n");

            openRoomCells.add(newLines.toString());
            openSwitchableRooms.add(null);
        }

        openRoomsListView.setItems(openRoomCells);
    }

    public void updateRegisteredRooms() {
        registeredRoomCells.clear();
        registeredSwitchableRooms.clear();
        availableRooms = hotelRooms.getRoomStatus();

        for (int i = 0; i < availableRooms.size(); ++i) {
            String floor = "FLOOR " + (i + 1) + ": " + hotelRooms.getFloor(i + 1) + "s\n\n";
            registeredRoomCells.add(floor);

            ArrayList<String> roomsOnFloor = availableRooms.get(i);
            registeredSwitchableRooms.add(null);

            for (int j = 0; j < roomsOnFloor.size(); ++j) {
                HotelRooms.RoomInfo roomInfo;

                if (roomsOnFloor.get(j).equals("" + guest.getGuestID())) {
                    roomInfo = hotelRooms.new RoomInfo(i + 1, j + 1);
                    registeredRoomCells.add("Room " + roomInfo.getRoomNumber());
                    registeredSwitchableRooms.add(roomInfo);
                }
            }

            if (i < availableRooms.size() - 1) {
                registeredRoomCells.add("\n\n");
                registeredSwitchableRooms.add(null);
            }
        }

        int numNewLinesToAdd = 38 - registeredRoomCells.size();

        if (numNewLinesToAdd > 0) { // To Fill In The Bottom Gap Of List View
            StringBuilder newLines = new StringBuilder();
            for (int i = 0; i < numNewLinesToAdd; i++)
                newLines.append("\n");

            registeredRoomCells.add(newLines.toString());
            registeredSwitchableRooms.add(null);
        }

        registeredRoomsListView.setItems(registeredRoomCells);
    }

    private void infoBox(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successful");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void alertBox(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Read");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }


    private void changeScene(ActionEvent actionEvent) throws Exception {
        FXMLLoader loginMenuLoader = new FXMLLoader(getClass().getResource("/fxml/boundary/hoteloptionspackage/HotelOptions.fxml"));
        Parent loginMenu = loginMenuLoader.load();

        hotelOptionsController = loginMenuLoader.getController();
        hotelOptionsController.setObjects(database, fileSystem, hotelRooms, managementSystem);
        hotelOptionsController.setGuest(guest);
        hotelOptionsController.setManager(manager);

        Stage mainStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene loginMenuScene = new Scene(loginMenu);
        mainStage.setScene(loginMenuScene);
        mainStage.show();
    }
}
