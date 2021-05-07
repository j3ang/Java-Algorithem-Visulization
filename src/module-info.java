module AlgorithmViz{
	requires javafx.graphics;
	requires javafx.controls;
	requires javafx.media;
	requires javafx.base;
	requires javafx.web;
	requires javafx.swing;
	requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires org.apache.commons.codec;
    requires dotenv.java;
    requires java.desktop;
    requires com.google.common;

	opens application to javafx.graphics;
	opens models;
	opens views;
	opens controllers;
	opens models.algorithms;
	opens models.algorithms.commons;

}
