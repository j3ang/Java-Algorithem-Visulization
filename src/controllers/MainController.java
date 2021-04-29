package controllers;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import models.Session;
import models.algorithms.commons.NumbersList;
import models.algorithms.commons.SortTask;
import models.algorithms.commons.SwapItem;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.lang.reflect.*;

/**
 * The type Main controller.
 */
public class MainController extends ConfigurationController implements Initializable {


    @FXML
    private BarChart<String, Integer> chart;
    @FXML
    private final XYChart.Series<String, Integer> chartData = new XYChart.Series();

    Session session = Session.getInstace();
    Thread sortingThread;
    SortTask sortTask;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
        setupControllers();
        setUpChart();
    }

    public void setupControllers() {
        Slider timeDurationSlider = (Slider) chart.getParent().getParent().lookup("#configSpeed");
        Text timeDurationText = (Text) chart.getParent().getParent().lookup("#configSpeedIndicator");
        timeDurationSliderListener(session, timeDurationSlider, timeDurationText);

        // Add start button
        Button startBtn = new Button("Start");
        startBtn.setId("startBtn");
        startBtn.setOnAction((e) -> {
            Platform.runLater(() -> {
                startSort();
            });
        });

        // Add save button
        Button saveBtn = new Button("Save");
        saveBtn.setOnAction((e) -> {
            System.out.println("Saving to database");
        });

        ((HBox) timeDurationSlider.getParent().getParent()).getChildren().add(startBtn);
        ((HBox) timeDurationSlider.getParent().getParent()).getChildren().add(saveBtn);

        ((HBox) timeDurationSlider.getParent().getParent()).setSpacing(40);
        ((HBox) timeDurationSlider.getParent().getParent()).setPrefWidth(1169);

    }

    public void setUpChart() {


        // session set up generated numbers
        int[] numbersArr = getRandomNumbers(session.getConfig().getNumbersSize(), 338);
        session.setGeneratedNumbers(numbersArr);
        HashMap<Integer, HashMap<Integer, String>> rainbowColoredNumbersMap
                = new NumbersList().getRainbowColoredNumbersMap(session.getGeneratedNumbers());

        // Set Chart
        // Set chart title
        chart.setTitle( session.getConfig().getAlgorithmSelected() );
        System.out.println("Selected algo: "+ session.getConfig().getAlgorithmSelected());

        for (int i = 0; i < rainbowColoredNumbersMap.size(); i++) {
            int number = (int) rainbowColoredNumbersMap.get(i).keySet().toArray()[0];
            chartData.getData().add(new XYChart.Data(String.valueOf(i + 1), number));
        }

        chart.getData().addAll(chartData);
        System.out.println("charData: " + chartData.getData());

        // Set bar color
        for (int j = 0; j < chartData.getData().size(); j++) {
            String color = rainbowColoredNumbersMap.get(j).values().toArray()[0].toString();
            XYChart.Data thisData = chartData.getData().get(j);
            thisData.getNode().setStyle("-fx-bar-fill: " + color + ";");

        }

        chart.setLegendVisible(false);
        chart.getXAxis().setLabel("Size of numbers");
        chart.getYAxis().setTickMarkVisible(false);
        chart.getXAxis().setTickMarkVisible(false);



        int[] arr = new int[chartData.getData().size()];
        for (int i = 0; i < chartData.getData().size(); i++) {
            arr[i] = chartData.getData().get(i).getYValue();
        }
    }

    private void startSort() {

        // Create new sorting Task based on selection
        sortTask = getSelectedSortTask();

        // Disable start button if no threads are running
        sortTask.runningProperty().addListener((observableValue, oldStatus, newStatus) -> {
            Button startBtn = (Button) chart.getParent().getParent().lookup("#startBtn");
            startBtn.disableProperty().set(newStatus);
        });

        sortingThread = new Thread(sortTask);
        session.setThread(sortingThread); // pass to session for termination purpose
        System.out.println("saved sorting thread in session: " + sortingThread);
        sortingThread.setDaemon(true);
        sortingThread.start();
        System.out.println("new sortingThread: " + sortingThread);
        System.out.println("Sorting is started: " + sortTask.getClass().getName());
        sortTask.valueProperty().addListener((ObservableValue<? extends SwapItem> observable, SwapItem oldValue, SwapItem newValue) -> {
            sortTask.getSwapCode(newValue).run();
        });
    }

    // Created sorting task from selection
    // https://stackoverflow.com/questions/1268817/create-new-object-from-a-string-in-java
    private SortTask getSelectedSortTask(){
        SortTask sortTask = null;
        try{
            String task = session.getConfig().getAlgorithmSelected();
            String className = task.replace("class ", "");
            Class<?> cl = Class.forName(className);
            Constructor con = cl.getConstructor();
            sortTask = (SortTask) con.newInstance();
            System.out.println("created sorting task: " + sortTask);

            sortTask.setChartData(chartData);
            sortTask.setSession(session);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return sortTask;
    }
    
}




