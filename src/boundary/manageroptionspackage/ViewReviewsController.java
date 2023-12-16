package boundary.manageroptionspackage;

import boundary.LoginMenuController;
import boundary.SetterMethods;
import control.HotelRooms;
import entity.Database;
import entity.FileSystem;
import entity.ManagementSystem;
import entity.Manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ViewReviewsController implements SetterMethods {
    private Database database;
    private FileSystem fileSystem;
    private Manager manager;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    private ManagerOptionsController managerOptionsController;
    @FXML
    private ListView listView;
    private ObservableList<String> guestReviews;


    @FXML
    private void initialize() {
        guestReviews = FXCollections.observableArrayList();
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

    public void displayReviews() {
        guestReviews.clear();
        guestReviews.setAll(manager.getReviews());
        listView.setItems(guestReviews);
    }

    @FXML
    private void goBackOnAction(ActionEvent actionEvent) throws Exception {
        changeScene(actionEvent);
    }


    private void changeScene(ActionEvent actionEvent) throws Exception {
        FXMLLoader loginMenuLoader = new FXMLLoader(getClass().getResource("/fxml/boundary/manageroptionspackage/ManagerOptions.fxml"));
        Parent loginMenu = loginMenuLoader.load();
        managerOptionsController = loginMenuLoader.getController();
        managerOptionsController.setObjects(database, fileSystem, hotelRooms, managementSystem);
        managerOptionsController.setManager(manager);

        Stage mainStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene loginMenuScene = new Scene(loginMenu);
        mainStage.setScene(loginMenuScene);
        mainStage.show();
    }
}
