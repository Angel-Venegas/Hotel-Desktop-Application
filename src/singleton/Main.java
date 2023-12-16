package singleton;

import boundary.LoginMenuController;
import control.HotelRooms;
import entity.Database;
import entity.FileSystem;
import entity.ManagementSystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private static Main mainSystem;
    private Database database;
    private FileSystem fileSystem;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loginMenuLoader = new FXMLLoader(getClass().getResource("/fxml/boundary/LoginMenu.fxml"));
        Parent mainLogin = loginMenuLoader.load();

        LoginMenuController longinMenu = loginMenuLoader.getController();
        longinMenu.setObjects(mainSystem.database, mainSystem.fileSystem, mainSystem.hotelRooms, mainSystem.managementSystem);

        Scene mainScene = new Scene(mainLogin);
        primaryStage.setMaximized(true);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {
        mainSystem = new Main();

        mainSystem.database = new Database();
        mainSystem.fileSystem = new FileSystem(mainSystem.database);
        mainSystem.hotelRooms = new HotelRooms(mainSystem.fileSystem);
        mainSystem.managementSystem = new ManagementSystem(mainSystem.fileSystem, mainSystem.hotelRooms);
        launch(args);
    }
}