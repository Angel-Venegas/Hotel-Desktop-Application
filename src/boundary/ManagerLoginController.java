package boundary;

import boundary.hoteloptionspackage.HotelOptionsController;
import boundary.manageroptionspackage.ManagerOptionsController;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class ManagerLoginController implements SetterMethods {
    private Database database;
    private FileSystem fileSystem;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    private Manager manager;
    private LoginMenuController loginMenuController;
    private HotelOptionsController hotelOptionsController;
    private ManagerOptionsController managerOptionsController;
    @FXML
    private  Button loginButton;
    @FXML
    private  Button cancelButton;
    @FXML
    private  TextField usernameTextField;
    @FXML
    private  PasswordField passwordTextField;


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
    private void cancelButtonOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/LoginMenu.fxml");
    }

    @FXML
    private void loginButtonOnAction(ActionEvent actionEvent) throws Exception {
        String userName = usernameTextField.getText();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (userName == null || userName.equals("")) {
            alert.setTitle("No Input Specified");
            alert.setHeaderText(null);
            alert.setContentText("Please Enter A Username");

            alert.showAndWait();
        }
        else if (userName.equals(manager.username)) {
            String userPassword = passwordTextField.getText();

            if (userPassword == null || userPassword.equals("")) {
                alert.setTitle("No Input Specified");
                alert.setHeaderText(null);
                alert.setContentText("Please Enter A Password");

                alert.showAndWait();
            }
            else if (userPassword.equals(manager.password)) {
                alert.setTitle("Login Successful");
                alert.setHeaderText(null);
                alert.setContentText("Congratulations! You have successfully logged in.");

                alert.showAndWait();

                changeScene(actionEvent, "/fxml/boundary/manageroptionspackage/ManagerOptions.fxml");
            } else {
                alert.setTitle("Wrong Password");
                alert.setHeaderText(null);
                alert.setContentText("Wrong Password. Please Try Again");

                alert.showAndWait();
            }
        }
        else {
            alert.setTitle("Wrong Username");
            alert.setHeaderText(null);
            alert.setContentText("Wrong Username. Please Try Again");

            alert.showAndWait();
        }
    }


    private void changeScene(ActionEvent actionEvent, String fxmlPath) throws Exception {
        FXMLLoader loginMenuLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent loginMenuRoot = loginMenuLoader.load();

        if (fxmlPath.equals("/fxml/boundary/manageroptionspackage/ManagerOptions.fxml")) {
            managerOptionsController = loginMenuLoader.getController();
            managerOptionsController.setObjects(database, fileSystem, hotelRooms, managementSystem);
            managerOptionsController.setManager(manager);
        }
        else if (fxmlPath.equals("/fxml/boundary/LoginMenu.fxml")) {
            loginMenuController = loginMenuLoader.getController();
            loginMenuController.setObjects(database, fileSystem, hotelRooms, managementSystem);
        }

        Stage mainStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(); // Retrieves The Stage Object From Which The Action Event Originated.
        Scene loginMenuScene = new Scene(loginMenuRoot);
        mainStage.setScene(loginMenuScene);
        mainStage.show();
    }
}