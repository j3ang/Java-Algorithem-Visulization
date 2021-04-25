package models;

import javafx.scene.Scene;

import java.util.Arrays;

// https://stackoverflow.com/questions/46508098/how-to-keep-user-information-after-login-in-javafx-desktop-application
public final class Session {

    private static Session instance;
    private static ConfigModel config;
    private static double drawingWrapperWidth,drawingWrapperHeight;
    private int[] generatedNumbers;
    private static Scene window;
    private double rectWidth;

    public Session(ConfigModel config) {
        Session.config = config;
    }

    public Session() {
        this.rectWidth = (getDrawingWrapperWidth() /  getConfig().getNumbersSize());
    }

    // Set Session
    public static Session getInstace(ConfigModel config ) {
        if(instance == null) {
            instance = new Session(config);
        }
        return instance;
    }

    public ConfigModel getConfig() {
        return config;
    }

    public Session setConfig(ConfigModel config) {
        Session.config = config;
        return this;
    }

    public static double getDrawingWrapperWidth() {
        return drawingWrapperWidth;
    }

    public static void setDrawingWrapperWidth(double drawingWrapperWidth) {
        Session.drawingWrapperWidth = drawingWrapperWidth;
    }

    public static double getDrawingWrapperHeight() {
        return drawingWrapperHeight;
    }

    public static void setDrawingWrapperHeight(double drawingWrapperHeight) {
        Session.drawingWrapperHeight = drawingWrapperHeight;
    }

    public int[] getGeneratedNumbers() {
        return generatedNumbers;
    }

    public Session setGeneratedNumbers(int[] generatedNumbers) {
        this.generatedNumbers = generatedNumbers;
        return this;
    }

    public double getRectWidth() {
        return rectWidth;
    }

    public Session setRectWidth(double rectWidth) {
        this.rectWidth = rectWidth;
        return this;
    }

    @Override
    public String toString() {
        return "Session{" + '\n' +
                "speedInterval=" + getConfig().getSpeedInterval() + ", " + '\n' +
                "drawingWrapper={ width="+ getDrawingWrapperWidth() + " height=" + getDrawingWrapperHeight() +" }, " + '\n' +
                "generatedNumbers={ rectWidth=" + (getDrawingWrapperWidth()/ getConfig().getNumbersSize()) + " size=" + getConfig().getNumbersSize()  + ", " +  Arrays.toString( getGeneratedNumbers() ) + "}" + '\n' +
                "}";
    }

    public void setWindow(Scene window) {
        Session.window = window;
    }

    public Scene getWindow() {
        return window;
    }
}