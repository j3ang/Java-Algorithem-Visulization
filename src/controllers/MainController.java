package controllers;

import Threading.SortTask;
import Threading.SwapItem;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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
    private BarChart<String, Integer> chart;
    private final XYChart.Series<String, Integer> chartData = new XYChart.Series();
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



    public void setUp() {
        initData(); // loggedIn user

        // session set up generated numbers
        int[] numbersArr = getRandomizedNumbers();
        session.setGeneratedNumbers( numbersArr );
        HashMap<Integer, HashMap<Integer, String>> rainbowColoredNumbersMap
                = new Visualizer().getRainbowColoredNumbersMap(session.getGeneratedNumbers());

        System.out.println("MainController.initialize(): " + session.toString());


        // Set Chart
        chart.getData().setAll(chartData);
        for (int i = 0; i < rainbowColoredNumbersMap.size(); i++){
            // add number value to series
            chartData.getData().add(new XYChart.Data(String.valueOf(i), rainbowColoredNumbersMap.get(i).keySet().toArray()[0] ) );
        }

        for ( int j = 0 ; j < chartData.getData().size(); j++ ){
//            System.out.println("Color " + rainbowColoredNumbersMap.get(j).values().toArray()[0]);
            String color = rainbowColoredNumbersMap.get(j).values().toArray()[0].toString();
            XYChart.Data thisData =  chartData.getData().get(j);
            thisData.getNode().setStyle("-fx-bar-fill: " + color +";");
        }

        chart.getYAxis().setTickMarkVisible(false);
        chart.getXAxis().setTickMarkVisible(false);

        int [] arr = new int[chartData.getData().size()];
        for (int i = 0; i < chartData.getData().size(); i++){
            arr[i] = chartData.getData().get(i).getYValue();
        }
        System.out.println("before sorted:" + Arrays.toString(arr));


    }

    private void startSort() {
        SortTask sortTask = new BubbleSort(chartData);
        Thread th = new Thread(sortTask);
        th.setDaemon(true);
        th.start();
        System.out.println("Sorting is started: " + sortTask.getClass().getName());
        sortTask.valueProperty().addListener((ObservableValue<? extends SwapItem> observable, SwapItem oldValue, SwapItem newValue) -> {
            sortTask.getSwapCode(newValue).run();


        });




    }

    // get random numbers from configured number size
    public int[] getRandomizedNumbers(){
        int[] randomNumbers = this.main.getRandomNumbers(session.getConfig().getNumbersSize(), 338);
        System.out.println("MainController.getRandomizedNumbers(): " +  Arrays.toString(randomNumbers));
        System.out.println("MainController.getRandomizedNumbers(): " + this.main.getNumbersSum(randomNumbers));
        return randomNumbers;
    }


}
