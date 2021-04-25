package controllers;

import Threading.SortTask;
import Threading.SwapItem;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import models.ConfigModel;
import models.MainModel;
import models.Session;
import models.Visualizer;
import models.algorithms.BubbleSort;

import java.net.URL;
import java.util.*;

public class MainController extends ConfigurationController implements Initializable {

    @FXML
    AnchorPane mainAnchor = new AnchorPane();
    @FXML
    XYChart  chart;

    XYChart.Series series =  new XYChart.Series();
    int[] generatedNumbers;

    Session session = Session.getInstace(new ConfigModel());
    MainModel main  = new MainModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUp(); // init
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    startSort();
                });
            }
        }, 2000);
    }

    private void startSort() {

        SortTask sortTask = new BubbleSort(new Visualizer().getRectangles());
        Thread th = new Thread(sortTask);
        th.setDaemon(true);
        th.start();

        System.out.println("Sorting is started: " + sortTask.getClass().getName());
        sortTask.valueProperty().addListener((ObservableValue<? extends SwapItem> observable, SwapItem oldValue, SwapItem newValue) -> {
            sortTask.getSwapCode(newValue).run();
            if (newValue != null) {
                System.out.println("the bigger rectangle posiition is at: " + newValue.getOriginalPos() + "\n" +
                        "second position: " + newValue.getSecondPos());
            }
        });

    }

    public void setUp() {
        initData(); // loggedIn user

        // session set up generated numbers
        int[] numbersArr = getRandomizedNumbers();
        session.setGeneratedNumbers( numbersArr );
        generatedNumbers  = session.getGeneratedNumbers();
        HashMap<Integer, Color> rainbowMap =
                new Visualizer().getRaindowColorMap(generatedNumbers);
        System.out.println("MainController.initialize(): " + session.toString());

        // Charts Setup - add number to series
        for (int i = 0; i < generatedNumbers.length; i++)
            series.getData().add(new XYChart.Data(String.valueOf(i), generatedNumbers[i]));
        chart.getData().addAll(series);

        // Charts Setup - add color to series data
        for ( Object data : series.getData() ){
            XYChart.Data thisData = (XYChart.Data)data;
            Color color = rainbowMap.get(thisData.getYValue());
            thisData.getNode().setStyle("-fx-bar-fill: " + toHexString(color) +";");
        }

    }

    // get random numbers from configured number size
    public int[] getRandomizedNumbers(){
        int[] randomNumbers = this.main.getRandomNumbers(session.getConfig().getNumbersSize(), 338);
        System.out.println("MainController.getRandomizedNumbers(): " +  Arrays.toString(randomNumbers));
        System.out.println("MainController.getRandomizedNumbers(): " + this.main.getNumbersSum(randomNumbers));
        return randomNumbers;
    }


    // Helpers
    private static String toHexString(Color color) {
        int r = ((int) Math.round(color.getRed()     * 255)) << 24;
        int g = ((int) Math.round(color.getGreen()   * 255)) << 16;
        int b = ((int) Math.round(color.getBlue()    * 255)) << 8;
        int a = ((int) Math.round(color.getOpacity() * 255));
        return String.format("#%08X", (r + g + b + a));
    }

}
