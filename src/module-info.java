module Hotel {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires javafx.swt;
    requires java.sql;

    exports boundary;
    opens boundary;

    exports singleton;
    opens singleton;

    exports boundary.hoteloptionspackage;
    opens boundary.hoteloptionspackage;

    exports boundary.manageroptionspackage;
    opens boundary.manageroptionspackage;

    exports entity;
    opens entity; // Open the entity package to javafx.base

}

