module ispw.project.project_ispw {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires okhttp3;
    requires com.google.gson;
    requires com.opencsv;
    requires java.sql;
    requires org.json;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;


    opens ispw.project.project_ispw.view.gui to javafx.fxml;

    opens ispw.project.project_ispw to javafx.fxml;
    exports ispw.project.project_ispw;

    opens ispw.project.project_ispw.model to com.google.gson;
    exports ispw.project.project_ispw.model;

    opens ispw.project.project_ispw.view.cli to javafx.fxml;

    opens ispw.project.project_ispw.controller.graphic.cli to javafx.fxml;
    exports ispw.project.project_ispw.controller.graphic.cli;

    opens ispw.project.project_ispw.controller.graphic.gui to javafx.fxml;
    exports ispw.project.project_ispw.controller.graphic.gui;

    exports ispw.project.project_ispw.dao;
    exports ispw.project.project_ispw.controller.graphic.cli.command;
    opens ispw.project.project_ispw.controller.graphic.cli.command to javafx.fxml;
}