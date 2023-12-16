package boundary.manageroptionspackage;

import boundary.SetterMethods;
import control.HotelRooms;
import entity.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;


public class ViewGuestInformationController implements SetterMethods {
    private Database database;
    private FileSystem fileSystem;
    private Manager manager;
    private HotelRooms hotelRooms;
    private ManagementSystem managementSystem;
    private ManagerOptionsController managerOptionsController;
    @FXML
    private TableView<Guest> tableView;
    @FXML
    private TableColumn<Guest, String> idColumn;
    @FXML
    private TableColumn<Guest, String> emailColumn;
    @FXML
    private TableColumn<Guest, String> firstNameColumn;
    @FXML
    private TableColumn<Guest, String> lastNameColumn;
    @FXML
    private TableColumn<Guest, String> usernameColumn;
    @FXML
    private TableColumn<Guest, String> passwordColumn;
    @FXML
    private TableColumn<Guest, String> floorColumn;
    @FXML
    private TableColumn<Guest, String> roomColumn;
    @FXML
    private TableColumn<Guest, String> totalGuestsColumn;
    @FXML
    private TableColumn<Guest, String> amountPaidColumn;
    @FXML
    private TableColumn<Guest, Integer> totalPaidColumn;



    @FXML
    private void initialize()  {

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


    public void updateColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("guestID"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("userPassword"));
        floorColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("roomChosen"));
        totalGuestsColumn.setCellValueFactory(new PropertyValueFactory<>("totalGuests"));
        amountPaidColumn.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        totalPaidColumn.setCellValueFactory(new PropertyValueFactory<>("totalPaid"));

        totalPaidColumn.setCellFactory(column -> {
            return new TableCell<Guest, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(String.valueOf(item));
                    }
                }
            };
        });


        ArrayList<String> guestInfo = fileSystem.returnBlock("***Guest Information:");
        ArrayList<String> hotelRoomInfo = fileSystem.returnBlock("***Hotel Room Information:");

        for (int i = 0; i < guestInfo.size(); i++) {
            String guestData[] = guestInfo.get(i).split("--");
            String roomData[] = hotelRoomInfo.get(i).split("--");

            String roomNumber = "";
            String[] roomNumbers = roomData[2].split(",");
            String roomNumbersPlusOne = "";

            if (!roomNumbers[0].equals("none"))
                for (int j = 0; j < roomNumbers.length; ++j) {
                    roomNumbersPlusOne += Integer.toString(Integer.parseInt(roomNumbers[j]) + 1);

                    if (j < roomNumbers.length - 1)
                        roomNumbersPlusOne += ",";
                }

            Guest guest = new Guest(Integer.parseUnsignedInt(guestData[0]), guestData[1], guestData[2], guestData[3], guestData[4], guestData[5], roomData[1], roomNumbersPlusOne, roomData[3], roomData[4]);

            tableView.getItems().add(guest);
        }

        setupTextWrapping(emailColumn);
        setupTextWrapping(firstNameColumn);
        setupTextWrapping(lastNameColumn);
        setupTextWrapping(usernameColumn);
        setupTextWrapping(passwordColumn);
        setupTextWrapping(floorColumn);
        setupTextWrapping(roomColumn);
        setupTextWrapping(totalGuestsColumn);
        setupTextWrapping(amountPaidColumn);
    }

    private void setupTextWrapping(TableColumn<Guest, String> column) {
        column.setCellFactory(new Callback<TableColumn<Guest, String>, TableCell<Guest, String>>() {
            @Override
            public TableCell<Guest, String> call(TableColumn<Guest, String> param) {
                TableCell<Guest, String> cell = new TableCell<Guest, String>() {
                    private final Text text = new Text();

                    {
                        setGraphic(text);
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            text.setText(null);
                            text.setWrappingWidth(0); // No wrapping
                            setGraphic(null);
                        } else {
                            text.setText(item);

                            // Calculate the wrapping width based on cell width
                            double wrappingWidth = param.getWidth() * 0.9; // 10% less
                            text.setWrappingWidth(wrappingWidth);

                            setGraphic(text);
                        }
                    }
                };
                return cell;
            }
        });
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
