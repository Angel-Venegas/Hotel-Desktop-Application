package boundary;

import control.HotelRooms;
import entity.Database;
import entity.FileSystem;
import entity.ManagementSystem;

public interface SetterMethods {
    public void setObjects(Database database, FileSystem fileSystem, HotelRooms hotelRooms, ManagementSystem managementSystem);
}
