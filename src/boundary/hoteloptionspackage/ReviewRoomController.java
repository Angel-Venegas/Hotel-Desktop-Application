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


public class ReviewRoomController implements SetterMethods {
    private Database database;
    private FileSystem fileSystem;
    private Guest guest;
    private Manager manager;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    private HotelOptionsController hotelOptionsController;
    @FXML
    private Label userCredentialLabel;
    @FXML
    private ListView registeredRoomsListView;
    @FXML
    private Button oneStarButton, twoStarButton, threeStarButton, fourStarButton, fiveStarButton;
    @FXML
    private TextArea reviewArea;
    private ObservableList<String> roomInfoList;
    private ArrayList<ArrayList<String>> availableRooms;
    private ArrayList<HotelRooms.RoomInfo> floorTracker; // Stores Floor And Room Numbers
    private HotelRooms.RoomInfo selectedFloorAndRoom;
    private String rating;


    @FXML
    private void initialize() {
        roomInfoList = FXCollections.observableArrayList();
        floorTracker = new ArrayList<>();

        reviewArea.setWrapText(true);
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
    public void submitOnAction(ActionEvent actionEvent) throws Exception {
        String review = reviewArea.getText();

        if (selectedFloorAndRoom == null) {
            alertBox("Please Select A Room");
            return;
        }
        else if (review == null || review.isEmpty() || review.matches("\\s*")) {
            alertBox("Please Write A Review");
            return;
        }
        else if (rating == null) {
            alertBox("Please Select A Rating");
            return;
        }

        manager.addReview(review);
        fileSystem.updateFile("***Hotel Room Reports:", "room report", new String[]{rating + "\n" + hotelRooms.getFloor(selectedFloorAndRoom.getFloor()) + ":\n\t\t" + review + "\n---"});

        thanksBox("Thank You For The Review!");
    }

    @FXML
    public void cancelOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent);
    }

    @FXML
    public void setRatingOnAction(ActionEvent event) throws Exception {
        Button clickedButton = (Button) event.getSource();

        rating = clickedButton.getText();
    }

    @FXML
    private void listViewCellClicked() {
        int selectedIndex = registeredRoomsListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0 && selectedIndex < floorTracker.size())
            selectedFloorAndRoom = floorTracker.get(selectedIndex);
    }

    public void updateListView() {
        roomInfoList.clear(); // Clear previous data
        availableRooms = hotelRooms.getRoomStatus();

        for (int i = 0; i < availableRooms.size(); ++i) {
            roomInfoList.add("FLOOR " + (i + 1) + ": " + hotelRooms.getFloor(i + 1) + "s\n\n");
            ArrayList<String> roomsOnFloor = availableRooms.get(i);
            floorTracker.add(null);

            for (int j = 0; j < roomsOnFloor.size(); ++j) {
                if (roomsOnFloor.get(j).equals("" + guest.getGuestID())) {
                    HotelRooms.RoomInfo roomInfo = hotelRooms.new RoomInfo(i + 1, j + 1);
                    floorTracker.add(roomInfo);
                    roomInfoList.add("Room " + roomInfo.getRoomNumber());
                }
            }

            if (i < availableRooms.size() - 1) {
                roomInfoList.add("\n\n");
                floorTracker.add(null);
            }
        }

        int numNewLinesToAdd = 22 - roomInfoList.size();

        if (numNewLinesToAdd > 0) { // To Fill In The Bottom Gap Of List View
            StringBuilder newLines = new StringBuilder();
            for (int i = 0; i < numNewLinesToAdd; i++)
                newLines.append("\n");

            roomInfoList.add(newLines.toString());
            floorTracker.add(null);
        }

        registeredRoomsListView.setItems(roomInfoList);
    }

    private void thanksBox(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thank You!");
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