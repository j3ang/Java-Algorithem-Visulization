module ALlgorithemeViz{
    requires  javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;
    requires org.apache.commons.codec;
    requires dotenv.java;

    opens application;
    opens models;
    opens views;
    opens controllers;
    opens models.algorithms;
}