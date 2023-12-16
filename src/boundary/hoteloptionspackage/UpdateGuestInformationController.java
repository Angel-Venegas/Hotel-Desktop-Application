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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;


public class UpdateGuestInformationController implements SetterMethods {
    private Database database;
    private FileSystem fileSystem;
    private Guest guest;
    private Manager manager;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    private HotelOptionsController hotelOptionsController;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
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
    private void cancelOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent);
    }

    @FXML
    private void updateOnAction(ActionEvent actionEvent) throws Exception {
        ArrayList<String> blockInfo = fileSystem.returnBlock("***Guest Information:");

        for (String line : blockInfo) {
            String[] lineSplit = line.split("--");

            if (!usernameTextField.getText().equals(guest.getUsername()) && usernameTextField.getText().equals(lineSplit[4])) { // If Username Exists In The File And Is Not The Current Guest's One
                alertBox("Read","Username Is Already Taken");
                return;
            }
        }

        if (emailTextField.getText().isBlank() || firstNameTextField.getText().isBlank() || lastNameTextField.getText().isBlank() || usernameTextField.getText().isBlank() || passwordTextField.getText().isBlank()) {
            alertBox("Blank Fields","Don't Leave Anything Blank");
            return;
        }


        if (confirmationBox("Are You Sure?")) {
            guest.setEmail(emailTextField.getText());
            guest.setFirstName(firstNameTextField.getText());
            guest.setLastName(lastNameTextField.getText());
            guest.setUsername(usernameTextField.getText());
            guest.setUserPassword(passwordTextField.getText());

            userCredentialLabel.setText( String.format("ID: %-8d %s", guest.getGuestID(), "Full Name: " + guest.getFirstName() + ", " + guest.getLastName()) );

            fileSystem.updateFile("***Guest Information:","update user", new String[]{Integer.toString(guest.getGuestID()), emailTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), usernameTextField.getText(), passwordTextField.getText()});

            alertBox("Information Saved","Your Information Has Been Saved");
        }
    }

    public void displayTextFields() {
        emailTextField.setText(guest.getEmail());
        firstNameTextField.setText(guest.getFirstName());
        lastNameTextField.setText(guest.getLastName());
        usernameTextField.setText(guest.getUsername());
        passwordTextField.setText(guest.getUserPassword());
    }

    private void alertBox(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

        String customStyle = "-fx-font-size: 16px;";
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().lookup(".content").setStyle(customStyle);

        alert.showAndWait();
    }


    private boolean confirmationBox(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);

        String customStyle = "-fx-font-size: 16px;";
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().lookup(".content").setStyle(customStyle);

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        java.util.Optional<ButtonType> result = alert.showAndWait(); // Show The Confirmation Dialog And Wait For User's Response

        return result.isPresent() && result.get() == yesButton; // False If User Click Cancel Or x Button
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
