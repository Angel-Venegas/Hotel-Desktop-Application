package boundary;

import control.HotelRooms;
import entity.Database;
import entity.FileSystem;
import entity.ManagementSystem;

import entity.Guest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;


public class RegisterUserController implements SetterMethods {
    private Database database;
    private FileSystem fileSystem;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    private LoginMenuController loginMenuController;
    @FXML
    private Button cancelButton;
    @FXML
    private Button registerButton;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField emailTextField;


    public void setObjects(Database database, FileSystem fileSystem, HotelRooms hotelRooms, ManagementSystem managementSystem) {
        this.database = database;
        this.fileSystem = fileSystem;
        this.hotelRooms = hotelRooms;
        this.managementSystem = managementSystem;
    }


    @FXML
    private void cancelOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent);
    }

    @FXML
    private void registerOnAction(ActionEvent actionEvent) throws Exception {
        ArrayList<String> blockInfo = fileSystem.returnBlock("***Guest Information:");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        boolean usernameNotTaken = true;

        for (String line : blockInfo) {
            String[] lineSplit = line.split("--");

            if (usernameTextField.getText().equals(lineSplit[4])) { // If Username Exists In The File
                alert.setTitle("Username Taken");
                alert.setHeaderText(null);
                alert.setContentText("Username already exists. Please choose another one");

                alert.showAndWait();
                usernameNotTaken = false;
            }
        }

        if (emailTextField.getText().trim().isEmpty() || firstNameTextField.getText().trim().isEmpty() || lastNameTextField.getText().trim().isEmpty() || usernameTextField.getText().trim().isEmpty() || passwordTextField.getText().trim().isEmpty()) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Warning");
            warning.setHeaderText(null);
            warning.setContentText("Do Not Leave Anything Blank");

            warning.showAndWait();
            return;
        }

        if (usernameNotTaken) { // Username Is Not Taken So Register It
            String[] formValues = new String[6];
            formValues[0] = Integer.toString(managementSystem.generateID());
            formValues[1] = emailTextField.getText();
            formValues[2] = firstNameTextField.getText();
            formValues[3] = lastNameTextField.getText();
            formValues[4] = usernameTextField.getText();
            formValues[5] = passwordTextField.getText();

            Guest guest = new Guest(Integer.parseInt(formValues[0]), formValues[1], formValues[2], formValues[3], formValues[4], formValues[5], "none", "none", "none","none");
            fileSystem.updateFile("***Guest Information:","register user", formValues);
            hotelRooms.updateHotelInfo(guest);
            managementSystem.updateSystemInfo(guest);

            alert.setTitle("Registered");
            alert.setHeaderText(null);
            alert.setContentText("Username Registered Successfully!");

            alert.showAndWait();

            changeScene(actionEvent);
        }
    }


    private void changeScene(ActionEvent actionEvent) throws Exception {
        FXMLLoader loginMenuLoader = new FXMLLoader(getClass().getResource("/fxml/boundary/LoginMenu.fxml"));
        Parent loginMenu = loginMenuLoader.load();

        loginMenuController = loginMenuLoader.getController();
        loginMenuController.setObjects(database, fileSystem, hotelRooms, managementSystem);

        Stage mainStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(); // Retrieves the Stage object from which the action event originated.
        Scene loginMenuScene = new Scene(loginMenu);
        mainStage.setScene(loginMenuScene);
        mainStage.show();
    }
}
