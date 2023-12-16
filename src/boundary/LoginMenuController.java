package boundary;

import control.HotelRooms;
import entity.Database;
import entity.FileSystem;
import entity.ManagementSystem;
import entity.Manager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class LoginMenuController implements SetterMethods {
    private GuestLoginController guestLoginController;
    private ManagerLoginController managerLoginController;
    private RegisterUserController registerUserController;
    private Database database;
    private FileSystem fileSystem;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    @FXML
    private Button guestButton;
    @FXML
    private Button managerButton;
    @FXML
    private Button registerButton;


    @Override
    public void setObjects(Database database, FileSystem fileSystem, HotelRooms hotelRooms, ManagementSystem managementSystem) {
        this.database = database;
        this.fileSystem = fileSystem;
        this.hotelRooms = hotelRooms;
        this.managementSystem = managementSystem;
    }


    @FXML
    private void guestLoginScene(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/GuestLogin.fxml");
    }

    @FXML
    private void managerLoginScene(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/ManagerLogin.fxml");
    }

    @FXML
    private void registerUserScene(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/RegisterUser.fxml");
    }


    private void changeScene(ActionEvent actionEvent, String fxmlPath) throws Exception {
        FXMLLoader loginMenuLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent loginMenu = loginMenuLoader.load();

        if (fxmlPath.equals("/fxml/boundary/GuestLogin.fxml")) {
            guestLoginController = loginMenuLoader.getController();
            guestLoginController.setObjects(database, fileSystem, hotelRooms, managementSystem);
        }
        else if (fxmlPath.equals("/fxml/boundary/ManagerLogin.fxml")) {
            managerLoginController = loginMenuLoader.getController();
            managerLoginController.setObjects(database, fileSystem, hotelRooms, managementSystem);
            managerLoginController.setManager(new Manager());
        }
        else if (fxmlPath.equals("/fxml/boundary/RegisterUser.fxml")) {
            registerUserController = loginMenuLoader.getController();
            registerUserController.setObjects(database, fileSystem, hotelRooms, managementSystem);
        }

        Stage mainStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(); // Retrieves the Stage object from which the action event originated.
        Scene loginMenuScene = new Scene(loginMenu);
        mainStage.setScene(loginMenuScene);
        mainStage.show();
    }
}
