package controllers;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.Session;
import models.algorithms.commons.NumbersList;
import models.algorithms.commons.SortTask;
import models.algorithms.commons.SwapItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * The type Main controller.
 */
public class MainController extends ConfigurationController implements Initializable {


    @FXML
    private BarChart<String, Integer> chart;
    @FXML
    private XYChart.Series<String, Integer> chartData ;
    @FXML
    private Text durationText;
	@FXML
	private VBox algoVBox;

    Session session = Session.getInstace();
    Thread sortingThread;
    SortTask sortTask;
    // https://gist.github.com/P7h/8691100


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
        setupControllers();
        setUpChart();
    }

    public void setupControllers() {

		// Numbers Slider
		Slider numbersSlider = (Slider) chart.getParent().getParent().lookup("#configNumber");
		Text numbersText = (Text) chart.getParent().getParent().lookup("#configNumberIndicator");

		setNumberSliderDefault(session, numbersSlider, numbersText);
		numbersSliderListener(session, numbersSlider, numbersText);

    	// Sleep Delays Slider
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

		ListView algoList = getAlgorithmsListView(algoVBox);
		setAlgolistAction(algoList,chart);


		// Other styling
		HBox numberSliderHBox = ((HBox) numbersSlider.getParent().getParent());
		numberSliderHBox.setSpacing(40);
		numberSliderHBox.setPrefWidth(1169);

		HBox timeDurationSliderHBox = ((HBox) timeDurationSlider.getParent().getParent());
		timeDurationSliderHBox.getChildren().add(startBtn);
		timeDurationSliderHBox.setSpacing(40);
		timeDurationSliderHBox.setPrefWidth(1169);

    }

    // overload to shuffle numbers
	void numbersSliderListener(Session session, Slider slider, Text indicator) {
		slider.valueProperty().addListener((observableValue, initValue, newValue) -> {
			int currentNumbers = newValue.intValue();
			System.out.println("Slider " + slider.getId() + " value changed:" + currentNumbers);
			indicator.setText(String.valueOf(currentNumbers));  // update indicator numbers
			session.getConfig().setNumbersSize(currentNumbers); // update session config numbers
			setUpChart();
		});
	}

    // Overload to add chart so title can be changed
	public void setAlgolistAction(ListView algoList, Chart chart){
		// Set session algorithms on click
		algoList.setOnMouseClicked(mouseEvent -> {
			try{
				String selectedAlgo = algoList.getSelectionModel().getSelectedItem().toString();
				System.out.println("Selected: " + selectedAlgo + " | Saving to session config...");
				session.getConfig().setAlgorithmSelected( selectedAlgo ) ;
				chart.setTitle(selectedAlgo);

				// if sorted then get new set of numbers
				if (session.getConfig().isSorted()){
					this.chart.getData().clear();
					this.chart.layout();
					System.out.println("before new dataL " + this.chart.getData());
					setUpChart();
				}
			} catch ( NullPointerException e ){
				System.out.println("No Algorithm Selected!");
			}
		});
	}


    public void setUpChart() {
		chart.getData().clear();
		chart.layout();
    	chartData = new XYChart.Series<>();
		chart.getData().add(chartData); // Apply chart series  to the chart

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
			chartData.getData().add(new XYChart.Data<>(String.valueOf(i + 1), number));
        }

        // Set bar color
        for (int j = 0; j < chartData.getData().size(); j++) {
            String color = rainbowColoredNumbersMap.get(j).values().toArray()[0].toString();
			XYChart.Data<String, Integer> thisData = chartData.getData().get(j);
            thisData.getNode().setStyle("-fx-bar-fill: " + color + ";");

			// repopulate the chartData
			// number keeps turning to double during the re-setup
			chartData.getData().get(j).setYValue(numbersArr[j]);
        }


        // Other Chart style
        chart.setLegendVisible(false);
        chart.getXAxis().setLabel("Size of numbers");
        chart.getYAxis().setTickMarkVisible(false);
        chart.getXAxis().setTickMarkVisible(false);




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

        // Add event listener on swap action
        sortTask.valueProperty().addListener((ObservableValue<? extends SwapItem> observable, SwapItem oldValue, SwapItem newValue) -> {
            sortTask.getSwapCode(newValue).run();
        });

        // Update time elapsed
        durationText.textProperty().bind(sortTask.messageProperty());
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






