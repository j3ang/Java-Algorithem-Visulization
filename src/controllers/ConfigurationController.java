package controllers;

import application.Main;
import com.google.gson.Gson;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.*;
import models.algorithms.commons.NumbersList;
import models.algorithms.commons.SortTask;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The type Configuration controller.
 */
public class ConfigurationController extends NumbersList implements Initializable {
    @FXML
    private ImageView avatarImageView;
    @FXML
    private VBox loggedinUserVBox;
    @FXML
    private VBox vTop;
    @FXML
    private VBox algoVBox;
    @FXML
    private AnchorPane configsAnchor;

    /**
     * The Session config.
     */

    Session session;

    public ConfigurationController() {
        this.session = Session.getInstace();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
        setUpConfigs();
    }

    private void setUpConfigs() {

    	// set up algorithms list views
		ListView algoList = getAlgorithmsListView(algoVBox);
		setAlgolistAction(algoList);

        // Parameters controllers
        for (int i = 0; i < configsAnchor.getChildren().size(); i++) {
            String id = configsAnchor.getChildren().get(i).getId();
            ConfigModel sessionConfig = session.getConfig();
            try {
                switch (id) {
                    case "configNumbers":
                        // Common slider configs
                        Slider numbersSlider = (Slider) configsAnchor.lookup("#" + id);
						System.out.println("min numbers: " + sessionConfig.MIN_NUMBERS);
                        sliderConfig(numbersSlider, sessionConfig.MAX_NUMBERS);
                        numbersSlider.setMin(sessionConfig.MIN_NUMBERS);
                        numbersSlider.setMax(sessionConfig.MAX_NUMBERS);

                        // Set default value
                        Text indicator = ((Text) configsAnchor.lookup("#configNumbersIndicator"));
                        indicator.setText(String.valueOf(sessionConfig.getNumbersSize()));
                        numbersSlider.setValue(sessionConfig.getNumbersSize());

                        // config number slider event listener
						numbersSliderListener(session, numbersSlider, indicator);
                        break;

                    case "configSpeed":
                        // Common slider configs
                        Slider speedSlider = (Slider) configsAnchor.lookup("#" + id);
                        Text inidcator = ((Text) configsAnchor.lookup("#configSpeedIndicator"));

                        // config number slider event listener
                        timeDurationSliderListener(session, speedSlider, inidcator);

                        break;
                }
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    void setNumberSliderDefault(Session session, Slider slider, Text indicator){
		// Set default value
		sliderConfig(slider, session.getConfig().MAX_NUMBERS);
		slider.setMin(session.getConfig().MIN_NUMBERS);
		slider.setMax(session.getConfig().MAX_NUMBERS);
		slider.setValue(session.getConfig().getNumbersSize());
		indicator.setText( session.getConfig().getNumbersSize() + " numbers");
	}

	void numbersSliderListener(Session session, Slider slider, Text indicator) {
		setNumberSliderDefault(session, slider, indicator);
		slider.valueProperty().addListener((observableValue, initValue, newValue) -> {
			int currentNumbers = newValue.intValue();
			System.out.println("Slider " + slider.getId() + " value changed:" + currentNumbers);
			indicator.setText(String.valueOf(currentNumbers));  // update indicator numbers
			session.getConfig().setNumbersSize(currentNumbers); // update session config numbers
		});
	}

	void timeDurationSliderListener(Session session, Slider slider, Text text) {
		// Set default value
		sliderConfig(slider, session.getConfig().MAX_SPEED_INTERVAL);
		slider.setMin(session.getConfig().MIN_SPEED_INTERVAL);
		slider.setMax(session.getConfig().MAX_SPEED_INTERVAL);
		slider.setValue(session.getConfig().getSpeedInterval());
		text.setText((session.getConfig().getSpeedInterval() * 1000.0) / 1000.0 + " millisec");

		slider.valueProperty().addListener((observableValue, initValue, newValue) -> {
			long currentIntervalSpeed = Double.valueOf((Double) newValue).longValue();
			System.out.println("Slider  " + slider.getId() + " value changed:" + currentIntervalSpeed);
			text.setText((currentIntervalSpeed * 1000.0) / 1000.0 + " millisec"); // update indicator speed interval
			session.getConfig().setSpeedInterval(currentIntervalSpeed);    // update session config speed interval
		});
	}


	public ListView getAlgorithmsListView(VBox algoVBox ){
		// Display available algorithms
		List<Class<SortTask>> sortTasks = getAlgorithms("models.algorithms");
		ListView algoList = new ListView();

		// Add to algorithms list view
		for (Object algo : sortTasks){
			algoList.getItems().add(algo.toString());
			System.out.println("algo.getClass(): " +  algo);
		}
		algoVBox.getChildren().add(algoList);
		algoVBox.setSpacing(40);

		// Default sorting algorithm to first item in the list if session did not set
		if ( session.getConfig().getAlgorithmSelected() == null ){
			algoList.getSelectionModel().selectFirst();
		} else {
			int index = 0;
			for ( Object algo : algoList.getItems() ){
				System.out.println("availABLE ALGO: " + algo);
				if (algo.toString().contains(session.getConfig().getAlgorithmSelected())){
					algoList.getSelectionModel().select(index);
				}
				index += 1;
			}
		}

		System.out.println("algoList.getSelectionModel().getSelectedItem().toString(): " + algoList);
		// Save selected algorithm task to session
		session.getConfig().setAlgorithmSelected( algoList.getSelectionModel().getSelectedItem().toString() );

		return algoList;
	}

	public void setAlgolistAction(ListView algoList){
		// Set session algorithms on click
		algoList.setOnMouseClicked(mouseEvent -> {
			try{
				System.out.println("Selected: " + algoList.getSelectionModel().getSelectedItem() + " | Saving to session config...");
				session.getConfig().setAlgorithmSelected( algoList.getSelectionModel().getSelectedItem().toString() ) ;

			} catch ( NullPointerException e ){
				System.out.println("No Algorithm Selected!");
			}
		});
	}


    // https://stackoverflow.com/questions/1810614/getting-all-classes-from-a-package
    private List<Class<SortTask>> getAlgorithms(String packageName)  {
		DaoModel dao = new DaoModel();
        List<Class<SortTask>> sortTasks = new ArrayList<>();
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            URL root = loader.getResource(packageName.replace(".", "/"));

            // running from jar file
            if (root.getProtocol().equals("jar")){
				System.out.println("getting algorithms from database...");
				try{
					String[] parsedOptionValue = getAlgos(dao).toString()
							.replaceAll("\\[", "")
							.replaceAll("\\]", "")
							.split(",");

					for ( String algo : parsedOptionValue ) {
						Class<?> cls = Class.forName(algo.trim());
						if (SortTask.class.isAssignableFrom(cls)){
							sortTasks.add((Class<SortTask>) cls);
						}
					}

				} catch ( NullPointerException e){
					System.out.println(e.getMessage());
				}

			} else { // running from IDE can read from file system
				// Filter .class files.
				assert root != null;
				File[] files = new File(root.getFile()).listFiles((dir, name) -> name.endsWith(".class"));
				Vector<String> algoList = new Vector<>(); // contains the class names

				// Find classes implementing ICommand.
				for (File file : files) {
					String className = file.getName().replaceAll(".class$", "");
					if ( !className.contains( " " ) ){
						System.out.println( "found: " + className );
						String taskName = packageName + "." + className;
						Class<?> cls = Class.forName(taskName);
						if (SortTask.class.isAssignableFrom(cls)) {
							sortTasks.add((Class<SortTask>) cls);
							algoList.add(taskName);
						}
					}
				}

				// save to database for jar file to read
				saveUpdateAlgo(dao, algoList);
            }

        } catch (ClassNotFoundException e ) {
			System.out.println(e.getMessage());
        }
		return sortTasks;
    }

	private Vector<String> getAlgos(DaoModel dao) {
		Vector algoList = new Vector();
    	try{
			String optionTable = dao.getTableName("options");
			String sql = "SELECT option_value from " + optionTable + " where option_key='algorithms';";
			ResultSet rs = new DbConnect().connect().createStatement().executeQuery(sql);
			algoList = dao.readData(rs, optionTable);
			return algoList;
		} catch (SQLException e){
    		e.printStackTrace();
		}

    	return algoList;
	}

	public void saveUpdateAlgo(DaoModel dao, Vector<String> algoList){
		// Save to Database
		String optionTable = dao.getTableName("options");
		String sql = "";

		ArrayList<String> option = new ArrayList<>();
		option.add("algorithms");
		option.add(algoList.toString());

		// algorithms option exists?
		if ( dao.rowExists(optionTable, "option_key", "algorithms") ){
			// do update
			sql = "UPDATE " + optionTable + " SET option_value='" + algoList + "' where " + dao.getTableCols(optionTable).get(1) + "='algorithms';";

		} else {
			sql = dao.prepareInsertStmt(
					optionTable,
					dao.getTableCols(optionTable),
					option,
					false);
		}

		System.out.println("insert option sql:" + sql);
		dao.executeStatement(optionTable, sql);
	}
    // Overload
    public void sliderConfig(Slider s, double max) {
        s.setShowTickLabels(true);
        s.setShowTickMarks(true);
        s.setMajorTickUnit(max / 5f);
        s.setBlockIncrement(max / 10f);
    }
    public void sliderConfig(Slider s, long max) {
        s.setShowTickLabels(true);
        s.setShowTickMarks(true);
        s.setMajorTickUnit(max / 5f);
        s.setBlockIncrement(max / 10f);
    }

    /**
     * Set up logged in user split menu button.
     *
     * @return the split menu button
     */
    public SplitMenuButton setUpLoggedInUser() {
        UserModel userModelLoggedIn = Main.userModelLoggedIn;

        Text roleText = (Text) loggedinUserVBox.lookup("#" + loggedinUserVBox.getChildren().get(0).getId());
        roleText.setText(userModelLoggedIn.getRoles().get(0));

        SplitMenuButton m = (SplitMenuButton) loggedinUserVBox.lookup("#" + loggedinUserVBox.getChildren().get(1).getId());
        String fullname = userModelLoggedIn.getFirstname() + " " + userModelLoggedIn.getLastname();
        m.setText(fullname);

        System.out.println(
                "ConfigurationController.setUpLoggedInUser(): " +
                        "\n" + "Fullname: " + fullname +
                        "\n" + "User ID: " + userModelLoggedIn.getUser_id() +
                        "\n" + "username: " + userModelLoggedIn.getUsername() +
                        "\n" + "Role: " + userModelLoggedIn.getRoles());

        return m;

    }

    /**
     * Init data.
     */
    public void initData() {

        Main.showImage("/assets/img/avatar.png", avatarImageView);
        SplitMenuButton m = setUpLoggedInUser();

        // Create Menu Items programmatically
        MenuItem users = new MenuItem("Manage Users");
        MenuItem history = new MenuItem("History");
        MenuItem configuration = new MenuItem("Configuration");
        MenuItem logout = new MenuItem("Logout");
        m.getItems().addAll(
                users,
                history,
                configuration,
                logout
        );

        // Set up event listeners for the menu items
        ArrayList<String> btnLoadScenes = new ArrayList<>();
        btnLoadScenes.add("users");
        btnLoadScenes.add("history");
        btnLoadScenes.add("configuration");
        btnLoadScenes.add("login");
        for (int i = 0; i < m.getItems().size(); i++) {
            int finalI = i;
            m.getItems().get(i).setOnAction((e) -> {
                // new alert
                Alert alert;
                Optional<ButtonType> result = Optional.of(ButtonType.OK);
                if (Session.getInstace().getThread() != null && Session.getInstace().getThread().isAlive()) {
                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Terminate " + Session.getInstace().getThread().getName());
                    alert.setContentText("Are you sure you want to leave?");
                    result = alert.showAndWait();
                }

                // result action
                if (result.get() == ButtonType.OK) {
                    // ... user chose OK
                    stopThread();
                    System.out.println("==============================================");
                    System.out.println("Loading " + btnLoadScenes.get(finalI) + ".fxml");
                    System.out.println("==============================================");
                    Main.loadScene(e, btnLoadScenes.get(finalI), true);
                } else {
                    // ... user chose CANCEL or closed the dialog
                    System.out.println("request cancelled");
                }

            });
        }

        // Could use capabilities for more flexibility
        // Restricting admin vs. user view
        if (!Main.userModelLoggedIn.getRoles().get(0).equals("Administrator") && !Main.userModelLoggedIn.getRoles().get(0).equals("Manager")) {
            users.setVisible(false);
        }

        // add eventListener for fx:id="closeBtn"
        Button closeBtn = (Button) vTop.lookup("#closeBtn");
        closeBtn.setOnAction((e) -> {
            System.out.println("Close Btn clicked.");
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.close();
        });

        // add eventListener for profile using the split menu main button
        m.setOnAction((e) -> {
            // new alert
            Alert alert;
            Optional<ButtonType> result = Optional.of(ButtonType.OK);
            if (Session.getInstace().getThread() != null && Session.getInstace().getThread().isAlive()) {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Terminate " + Session.getInstace().getThread().getName());
                alert.setContentText("Are you sure you want to leave?");
                result = alert.showAndWait();
            }

            // result action
            if (result.get() == ButtonType.OK) {
                // ... user chose OK
                stopThread();
                System.out.println("==============================================");
                System.out.println("Loading " + "profile" + ".fxml");
                System.out.println("==============================================");
                Main.loadScene(e, "profile", true);
            } else {
                // ... user chose CANCEL or closed the dialog
                System.out.println("request cancelled");
            }
        });


    }

    private void stopThread() {
        try {
            System.out.println("Interrupting running thread.");
            Session.getInstace().getThread().interrupt();
        } catch (NullPointerException nullException) {
            System.out.println("No active running thread in session.");
        }

    }

    /**
     * Run action.
     *
     * @param evt the evt
     * @throws IOException the io exception
     */
    public void runAction(ActionEvent evt) throws IOException {
        Main.loadScene(evt, "main", false);
    }

    private String getAlgorithmName(String algoClass) {
        String Regex = "(\\.)(\\w+)(\\.)(\\w+)";
        Pattern pattern = Pattern.compile(Regex);

        Matcher matcher = pattern.matcher(algoClass);
        if (matcher.find()) {
            System.out.println("found algo");
            String name = matcher.group(4);
            System.out.println(name);
            return name;
        }
        return "";
    }
}


