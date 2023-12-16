package boundary.hoteloptionspackage;

import boundary.SetterMethods;
import boundary.LoginMenuController;
import control.HotelRooms;
import entity.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class HotelOptionsController implements SetterMethods {
    private Database database;
    private FileSystem fileSystem;
    private Guest guest;
    private Manager manager;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    private RegisterRoomController registerRoomController;
    private CancelRoomController cancelRoomController;
    private ChangeRoomController changeRoomController;
    private ViewRoomsController viewRoomsController;
    private LoginMenuController loginMenuController;
    private ReviewRoomController reviewRoomController;
    private UpdateGuestInformationController updateGuestInformationController;
    @FXML
    private Label userCredentialLabel;


    @FXML
    private void initialize() {

    }


    public void setGuest(Guest guest) {
        this.guest = guest;
        userCredentialLabel.setText( String.format("ID: %-8d %s", guest.getGuestID(), "Full Name: " + guest.getFirstName() + ", " + guest.getLastName()) );
    }

    public void setManager(Manager manager) {
        this.manager = manager;
        manager.assignManagementSystem(managementSystem);
    }

    @Override
    public void setObjects(Database database, FileSystem fileSystem, HotelRooms hotelRooms, ManagementSystem managementSystem) {
        this.database = database;
        this.fileSystem = fileSystem;
        this.hotelRooms = hotelRooms;
        this.managementSystem = managementSystem;
    }


    @FXML
    private void registerRoomOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/hoteloptionspackage/RegisterRoom.fxml");
    }

    @FXML
    private void cancelRoomOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/hoteloptionspackage/CancelRoom.fxml");
    }

    @FXML
    private void changeRoomOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/hoteloptionspackage/ChangeRoom.fxml");
    }

    @FXML
    private void viewRoomsOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/hoteloptionspackage/ViewRooms.fxml");
    }

    @FXML
    private void reviewRoomOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/hoteloptionspackage/ReviewRoom.fxml");
    }

    @FXML
    private void updateGuestInfoOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/hoteloptionspackage/UpdateGuestInformation.fxml");
    }

    @FXML
    private void logoutOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/LoginMenu.fxml");
    }


    private void changeScene(ActionEvent actionEvent, String fxmlPath) throws Exception {
        FXMLLoader loginMenuLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent loginMenu = loginMenuLoader.load();

        if (fxmlPath.equals("/fxml/boundary/hoteloptionspackage/RegisterRoom.fxml")) {
            registerRoomController = loginMenuLoader.getController();
            registerRoomController.setObjects(database, fileSystem, hotelRooms, managementSystem);
            registerRoomController.setGuest(guest);
            registerRoomController.setManager(manager);
            registerRoomController.updateListView();
        }
        else if (fxmlPath.equals("/fxml/boundary/hoteloptionspackage/CancelRoom.fxml")) {
            cancelRoomController = loginMenuLoader.getController();
            cancelRoomController.setObjects(database, fileSystem, hotelRooms, managementSystem);
            cancelRoomController.setGuest(guest);
            cancelRoomController.setManager(manager);
            cancelRoomController.updateListView();
        }
        else if (fxmlPath.equals("/fxml/boundary/hoteloptionspackage/ChangeRoom.fxml")) {
            changeRoomController = loginMenuLoader.getController();
            changeRoomController.setObjects(database, fileSystem, hotelRooms, managementSystem);
            changeRoomController.setGuest(guest);
            changeRoomController.setManager(manager);
            changeRoomController.updateRegisteredRooms();
        }
        else if (fxmlPath.equals("/fxml/boundary/hoteloptionspackage/ViewRooms.fxml")) {
            viewRoomsController = loginMenuLoader.getController();
            viewRoomsController.setObjects(database, fileSystem, hotelRooms, managementSystem);
            viewRoomsController.setGuest(guest);
            viewRoomsController.setManager(manager);
        }
        else if (fxmlPath.equals("/fxml/boundary/hoteloptionspackage/ReviewRoom.fxml")) {
            reviewRoomController = loginMenuLoader.getController();
            reviewRoomController.setObjects(database, fileSystem, hotelRooms, managementSystem);
            reviewRoomController.setGuest(guest);
            reviewRoomController.setManager(manager);
            reviewRoomController.updateListView();
        }
        else if (fxmlPath.equals("/fxml/boundary/hoteloptionspackage/UpdateGuestInformation.fxml")) {
            updateGuestInformationController = loginMenuLoader.getController();
            updateGuestInformationController.setObjects(database, fileSystem, hotelRooms, managementSystem);
            updateGuestInformationController.setGuest(guest);
            updateGuestInformationController.setManager(manager);
            updateGuestInformationController.displayTextFields();
        }
        else if (fxmlPath.equals("/fxml/boundary/LoginMenu.fxml")) {
            loginMenuController = loginMenuLoader.getController();
            loginMenuController.setObjects(database, fileSystem, hotelRooms, managementSystem);
        }


        Stage mainStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(); // Retrieves the Stage object from which the action event originated.
        Scene loginMenuScene = new Scene(loginMenu);
        mainStage.setScene(loginMenuScene);
        mainStage.show();
    }
}