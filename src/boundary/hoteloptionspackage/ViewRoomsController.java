package boundary.hoteloptionspackage;

import boundary.SetterMethods;
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

public class ViewRoomsController implements SetterMethods {
    private Database database;
    private FileSystem fileSystem;
    private Guest guest;
    private Manager manager;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    private HotelOptionsController hotelOptionsController;
    @FXML
    private Label userCredentialLabel;


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
    private void goBackOnAction(ActionEvent actionEvent) throws Exception  {
        changeScene(actionEvent);
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
