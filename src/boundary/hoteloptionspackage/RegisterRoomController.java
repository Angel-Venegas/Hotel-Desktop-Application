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
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;


public class RegisterRoomController implements SetterMethods {
    private Database database;
    private FileSystem fileSystem;
    private HotelRooms hotelRooms;
    private Guest guest;
    private Manager manager;
    private ManagementSystem managementSystem;
    private HotelOptionsController hotelOptionsController;
    @FXML
    private ListView roomListView;
    @FXML
    private ComboBox<String> floorDropDown;
    @FXML
    private ComboBox<String> roomDropDown;
    @FXML
    private ComboBox<String> numPeopleDropDown;
    @FXML
    private ComboBox<String> paymentDropDown;
    @FXML
    private Label userCredentialLabel;
    private ObservableList<String> roomInfoList;
    private ArrayList<ArrayList<String>> availableRooms;
    private String iDFullName;


    @FXML
    private void initialize() {
        floorDropDown.getItems().addAll("Floor 1 - Single Rooms", "Floor 2 - Double Rooms", "Floor 3 - Triple Rooms", "Floor 4 - Quad Rooms", "Floor 5 - Deluxe Rooms", "Floor 6 - Super Deluxe Rooms");
        floorDropDown.setValue("Floors With Room Types");

        roomDropDown.setValue("Select A Floor First");

        roomInfoList = FXCollections.observableArrayList();

        numPeopleDropDown.setValue("Select A Floor First");

        paymentDropDown.setValue("Select A Floor First");

        floorDropDown.setOnAction(event -> updateDropdowns());
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


    public void updateListView() {
        roomInfoList.clear(); // Clear previous data
        availableRooms = hotelRooms.getRoomStatus();

        for (int i = 0; i < availableRooms.size(); ++i) {
            roomInfoList.add("FLOOR " + (i + 1) + ": " + hotelRooms.getFloor(i + 1) + "s\n\n");
            roomInfoList.add("────────────────────────────────────────");

            for (int j = 0; j < availableRooms.get(i).size(); ++j)
                if (availableRooms.get(i).get(j).equals("Open"))
                    roomInfoList.add("Room " + (j + 1));

            roomInfoList.add("\n\n");
        }

        roomListView.setItems(roomInfoList);
    }

    private void updateRoomDropDown(int floor) {
        roomDropDown.setValue(null);
        roomDropDown.getItems().clear();
        roomDropDown.setValue("Rooms");

        availableRooms = hotelRooms.getRoomStatus();

        for (int i = 0; i < availableRooms.get(floor).size(); ++i)
            if (availableRooms.get(floor).get(i).equals("Open"))
                roomDropDown.getItems().add("Room " + (i + 1));
    }

    private void updateDropdowns() { // numPeopleDropDown and paymentDropDown
        String selectedFloor = floorDropDown.getValue();

        if (!selectedFloor.equals("Floors With Room Types")) {
            numPeopleDropDown.setValue(null);
            numPeopleDropDown.getItems().clear();
            numPeopleDropDown.setValue("Number Of People");

            paymentDropDown.setValue(null);
            paymentDropDown.getItems().clear();
            paymentDropDown.setValue("Payment");

            if (selectedFloor.equals("Floor 1 - Single Rooms")) {
                updateRoomDropDown(0);
                numPeopleDropDown.getItems().addAll("1");
                paymentDropDown.getItems().addAll("1000");
            } else if (selectedFloor.equals("Floor 2 - Double Rooms")) {
                updateRoomDropDown(1);
                numPeopleDropDown.getItems().addAll("1", "2");
                paymentDropDown.getItems().addAll("2000");
            } else if (selectedFloor.equals("Floor 3 - Triple Rooms")) {
                updateRoomDropDown(2);
                numPeopleDropDown.getItems().addAll("1", "2", "3");
                paymentDropDown.getItems().addAll("3000");
            } else if (selectedFloor.equals("Floor 4 - Quad Rooms")) {
                updateRoomDropDown(3);
                numPeopleDropDown.getItems().addAll("1", "2", "3", "4");
                paymentDropDown.getItems().addAll("4000");
            } else if (selectedFloor.equals("Floor 5 - Deluxe Rooms")) {
                updateRoomDropDown(4);
                numPeopleDropDown.getItems().addAll("1", "2", "3", "4", "5", "6");
                paymentDropDown.getItems().addAll("6000");
            } else if (selectedFloor.equals("Floor 6 - Super Deluxe Rooms")) {
                updateRoomDropDown(5);
                numPeopleDropDown.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
                paymentDropDown.getItems().addAll("9000");
            }
            roomDropDown.requestLayout();
            numPeopleDropDown.requestLayout();
            paymentDropDown.requestLayout();
        }
    }

    @FXML
    private void registerOnAction(ActionEvent actionEvent) throws Exception {
        if (floorDropDown.getValue().equals("Floors With Room Types") || roomDropDown.getValue().equals("Rooms") || numPeopleDropDown.getValue().equals("Number Of People") || paymentDropDown.getValue().equals("Payment")) {
            alertBox("Please fill out all information before registering.");
            return;
        }

        hotelRooms.registerRoom(guest.getGuestID(), Integer.parseInt(floorDropDown.getValue().split(" ")[1]) - 1, Integer.parseInt(roomDropDown.getValue().split(" ")[1]) - 1,
                Integer.parseInt(numPeopleDropDown.getValue()), Integer.parseInt(paymentDropDown.getValue()) );

        updateDropdowns();
        updateRoomDropDown(Integer.parseInt(floorDropDown.getValue().split(" ")[1]) - 1);
        updateListView();

        alertBox("Room Registered Successfully!");
    }


    @FXML
    private void goBackOnAction(ActionEvent actionEvent) throws Exception  {
        changeScene(actionEvent);
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
