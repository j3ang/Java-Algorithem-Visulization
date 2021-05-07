package models;

import javafx.scene.Scene;
import javafx.scene.chart.XYChart;

import java.time.Duration;
import java.util.Arrays;

// https://stackoverflow.com/questions/46508098/how-to-keep-user-information-after-login-in-javafx-desktop-application
public final class Session{

    private static Session instance;
    private static ConfigModel config;
    private int[] generatedNumbers;
    private Thread thread;
    private String duration;

    public Session(ConfigModel config) {
        Session.config = config;
    }

    // Set Session
    public static Session getInstace() {
        if(instance == null) {
            instance = new Session(new ConfigModel());
        }
        return instance;
    }


    public ConfigModel getConfig() {
        return config;
    }

    public int[] getGeneratedNumbers() {
        return generatedNumbers;
    }

    public Session setGeneratedNumbers(int[] generatedNumbers) {
        this.generatedNumbers = generatedNumbers;
        return this;
    }


    @Override
    public String toString() {
        return "Session{" + '\n' +
                "speedInterval=" + getConfig().getSpeedInterval() + ", " + '\n' +
                "generatedNumbers={ size=" + getConfig().getNumbersSize()  + ", " +  Arrays.toString( getGeneratedNumbers() ) + "}" + '\n' +
                "}";
    }


    public void setThread(Thread thread) {
        this.thread = thread;
    }
    public Thread getThread() {
        return thread;
    }

    public void setDuration(String duration){
        this.duration = duration;
    }
    public String getDuration() {
        return duration;
    }
}
