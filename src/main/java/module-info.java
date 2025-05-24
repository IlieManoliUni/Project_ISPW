// File: src/main/java/module-info.java
module ispw.project.project_ispw {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome; // Keep this if you use FontAwesome icons
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires okhttp3; // Needed for HTTP requests
    requires com.google.gson; // Needed for JSON parsing
    requires com.opencsv; // Needed for CSV operations
    requires java.sql; // Needed for JDBC database connections
    requires org.json; // Often used for JSON processing
    requires java.desktop; // Often needed for AWT/Swing interop or desktop features
    requires com.fasterxml.jackson.databind; // Another common JSON library

    // Open specific packages for FXML reflection and other reflection/access needs
    opens ispw.project.project_ispw.view.gui to javafx.fxml;
    exports ispw.project.project_ispw.view.gui; // Export if other modules or tests need access

    opens ispw.project.project_ispw to javafx.fxml; // For MainApp and other top-level classes/resources
    exports ispw.project.project_ispw;

    opens ispw.project.project_ispw.model to com.google.gson; // Open model for GSON
    exports ispw.project.project_ispw.model; // Export if other modules or tests need access

    opens ispw.project.project_ispw.view.cli to javafx.fxml; // For CLI FXML and related views
    exports ispw.project.project_ispw.view.cli;

    opens ispw.project.project_ispw.controller.graphic.cli to javafx.fxml; // For CLI graphic controllers
    exports ispw.project.project_ispw.controller.graphic.cli;

    opens ispw.project.project_ispw.controller.graphic.gui to javafx.fxml; // For GUI graphic controllers
    exports ispw.project.project_ispw.controller.graphic.gui;

    exports ispw.project.project_ispw.dao;
    exports ispw.project.project_ispw.controller.graphic.cli.command;
    opens ispw.project.project_ispw.controller.graphic.cli.command to javafx.fxml; // Export DAO if it's used by other modules
}