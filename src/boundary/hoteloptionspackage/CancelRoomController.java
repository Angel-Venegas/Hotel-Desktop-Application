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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CancelRoomController implements SetterMethods {
    private Guest guest;
    private Manager manager;
    private Database database;
    private FileSystem fileSystem;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    private HotelOptionsController hotelOptionsController;
    @FXML
    private ListView registeredRoomsListView;
    @FXML
    private ComboBox<String> cancelRoomDropDown;
    @FXML
    private Label userCredentialLabel;
    private ObservableList<String> roomInfoList;
    private ArrayList<ArrayList<String>> availableRooms;


    @FXML
    private void initialize() {
        roomInfoList = FXCollections.observableArrayList();

        cancelRoomDropDown.setValue("Select A Room To Cancel");
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
            roomInfoList.add("────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");

            for (int j = 0; j < availableRooms.get(i).size(); ++j)
                if (availableRooms.get(i).get(j).equals("" + guest.getGuestID())) {
                    roomInfoList.add("Room " + (j + 1));
                    cancelRoomDropDown.getItems().add("Floor " + (i + 1) + " " + "Room " + (j + 1));
                }

            roomInfoList.add("\n\n");
        }

        registeredRoomsListView.setItems(roomInfoList);
    }

    private void updateCancelDropDown() {
        String selectedRoom = cancelRoomDropDown.getValue();

        if (!selectedRoom.equals("Select A Room To Cancel")) {
            cancelRoomDropDown.setValue(null);
            cancelRoomDropDown.getItems().clear();
            cancelRoomDropDown.setValue("Select A Room To Cancel");
        }
    }


    @FXML
    private void cancelRoomOnAction(ActionEvent actionEvent) throws Exception  {
        if (cancelRoomDropDown.getValue().equals("Select A Room To Cancel")) {
            alertBox("Please Select A Room To Cancel");
            return;
        }

        hotelRooms.cancelRoom( guest.getGuestID(), Integer.parseInt(cancelRoomDropDown.getValue().split( " ")[1]) - 1, Integer.parseInt(cancelRoomDropDown.getValue().split( " ")[3]) - 1 );

        updateCancelDropDown();
        updateListView();

        alertBox("Room Canceled Successfully!");
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
