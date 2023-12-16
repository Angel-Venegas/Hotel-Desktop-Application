package boundary.manageroptionspackage;

import boundary.SetterMethods;
import boundary.LoginMenuController;
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
import javafx.stage.Stage;

public class ManagerOptionsController implements SetterMethods {
    private Database database;
    private FileSystem fileSystem;
    private Manager manager;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    private LoginMenuController loginMenuController;
    private ViewReviewsController viewReviewsController;
    private ViewGuestInformationController viewGuestInformationController;


    @FXML
    private void initialize() {

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
    private void logoutOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/LoginMenu.fxml");
    }

    @FXML
    private void viewReviewsOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/manageroptionspackage/ViewReviews.fxml");
    }

    @FXML
    private void viewGuestInfoOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent, "/fxml/boundary/manageroptionspackage/ViewGuestInformation.fxml");
    }


    private void changeScene(ActionEvent actionEvent, String fxmlPath) throws Exception {
        FXMLLoader loginMenuLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent loginMenu = loginMenuLoader.load();

        if (fxmlPath.equals("/fxml/boundary/manageroptionspackage/ViewReviews.fxml")) {
            viewReviewsController = loginMenuLoader.getController();
            viewReviewsController.setObjects(database, fileSystem, hotelRooms, managementSystem);
            viewReviewsController.setManager(manager);
            viewReviewsController.displayReviews();
        }
        else if (fxmlPath.equals("/fxml/boundary/manageroptionspackage/ViewGuestInformation.fxml")) {
            viewGuestInformationController = loginMenuLoader.getController();
            viewGuestInformationController.setObjects(database,fileSystem, hotelRooms, managementSystem);
            viewGuestInformationController.setManager(manager);
            viewGuestInformationController.updateColumns();
        }
        else if (fxmlPath.equals("/fxml/boundary/LoginMenu.fxml")) {
            loginMenuController = loginMenuLoader.getController();
            loginMenuController.setObjects(database, fileSystem, hotelRooms, managementSystem);
        }


        Stage mainStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene loginMenuScene = new Scene(loginMenu);
        mainStage.setScene(loginMenuScene);
        mainStage.show();
    }
}
