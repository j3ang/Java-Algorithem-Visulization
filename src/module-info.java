module ALlgorithemeViz{
    requires  javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;
    requires org.apache.commons.codec;
    requires dotenv.java;
    requires java.desktop;
    requires com.google.common;

    opens application;
    opens models;
    opens views;
    opens controllers;
    opens models.algorithms;
    opens models.algorithms.commons;
}