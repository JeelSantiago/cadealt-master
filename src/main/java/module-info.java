module com.cadealt {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    // ControlsFX
    requires org.controlsfx.controls;

    // SQLite
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    // Exports packages para o JavaFX
    exports com.cadealt;
    exports com.cadealt.controller;
    exports com.cadealt.model;
    exports com.cadealt.view;

    // Opens packages para FXML reflection
    opens com.cadealt to javafx.fxml;
    opens com.cadealt.controller to javafx.fxml;
    opens com.cadealt.view to javafx.fxml;
    opens com.cadealt.model to javafx.base;
}
