package boundary;

import boundary.hoteloptionspackage.HotelOptionsController;
import control.HotelRooms;
import entity.*;

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


public class GuestLoginController implements SetterMethods {
    private Database database;
    private FileSystem fileSystem;
    private HotelRooms hotelRooms;
    private Guest guest;
    private ManagementSystem managementSystem;
    private LoginMenuController loginMenuController;
    private HotelOptionsController hotelOptionsController;
    @FXML
    private  Button loginButton;
    @FXML
    private  Button cancelButton;
    @FXML
    private  TextField usernameTextField;
    @FXML
    private  PasswordField passwordTextField;
    String userID;


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
        String username = usernameTextField.getText();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (username == null || username.equals("")) { // No Username Provided
            alert.setTitle("No Input Specified");
            alert.setHeaderText(null);
            alert.setContentText("Please Enter A Username");

            alert.showAndWait();
        }
        else if (managementSystem.confirmUsername(username)) { // Username Exists In The System
            String password = passwordTextField.getText();

            if (password == null || password.equals("")) { // No Password Provided
                alert.setTitle("No Input Specified");
                alert.setHeaderText(null);
                alert.setContentText("Please Enter A Password");

                alert.showAndWait();
            }
            else if (managementSystem.confirmPassword(username, password)) { // Password Exists In The System
                alert.setTitle("Login Successful");
                alert.setHeaderText(null);
                alert.setContentText("Congratulations! You have successfully logged in.");

                alert.showAndWait();

                guest = managementSystem.retrieveGuest(username, password);

                changeScene(actionEvent, "/fxml/boundary/hoteloptionspackage/HotelOptions.fxml");
            } else { // Wrong Password
                alert.setTitle("Wrong Password");
                alert.setHeaderText(null);
                alert.setContentText("Wrong Password. Please Try Again");

                alert.showAndWait();
            }
        }
        else { // Wrong Username
            alert.setTitle("Wrong Username");
            alert.setHeaderText(null);
            alert.setContentText("Wrong Username. Please Try Again");

            alert.showAndWait();
        }

    }


    private void changeScene(ActionEvent actionEvent, String fxmlPath) throws Exception {
        FXMLLoader loginMenuLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent loginMenuRoot = loginMenuLoader.load();

        if (fxmlPath.equals("/fxml/boundary/hoteloptionspackage/HotelOptions.fxml")) {
            hotelOptionsController = loginMenuLoader.getController();
            hotelOptionsController.setObjects(database, fileSystem, hotelRooms, managementSystem);
            hotelOptionsController.setGuest(guest);
            hotelOptionsController.setManager(new Manager());
        }
        else if (fxmlPath.equals("/fxml/boundary/LoginMenu.fxml")) {
            loginMenuController = loginMenuLoader.getController();
            loginMenuController.setObjects(database, fileSystem, hotelRooms, managementSystem);
        }

        Stage mainStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene loginMenuScene = new Scene(loginMenuRoot);
        mainStage.setScene(loginMenuScene);
        mainStage.show();
    }
}