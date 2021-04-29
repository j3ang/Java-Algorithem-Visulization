package controllers;

import application.Main;
import javafx.application.Platform;
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
import models.ConfigModel;
import models.Session;
import models.UserModel;
import models.algorithms.commons.NumbersList;
import models.algorithms.commons.SortTask;

import java.awt.font.OpenType;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;


/**
 * The type Configuration controller.
 */
public class ConfigurationController extends NumbersList implements Initializable  {
    @FXML
    private ImageView avatarImageView;
    @FXML
    private VBox loggedinUserVBox;
    @FXML
    private VBox vTop;
    @FXML
    private AnchorPane configsAnchor;

    /**
     * The Session config.
     */
    ConfigModel sessionConfig;
    Session  session;

    public ConfigurationController() {
        this.session = Session.getInstace();
        this.sessionConfig = session.getConfig();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
        setUpParams();

    }

    private void setUpParams() {
        for ( int i = 0; i <  configsAnchor.getChildren().size(); i++ ){
            String id = configsAnchor.getChildren().get(i).getId();
            try {
                switch (id) {
                    case "configNumbers":
                        // Common slider configs
                        Slider numbersSlider = (Slider)configsAnchor.lookup("#" + id);
                        sliderConfig(numbersSlider, (double) sessionConfig.MAX_NUMBERS);
                        numbersSlider.setMin(sessionConfig.MIN_NUMBERS);
                        numbersSlider.setMax(sessionConfig.MAX_NUMBERS);

                        // Set default value
                        Text indicator = ((Text)configsAnchor.lookup("#configNumbersIndicator"));
                        indicator.setText(String.valueOf(sessionConfig.getNumbersSize()));
                        numbersSlider.setValue(sessionConfig.getNumbersSize());

                        // config number slider event listener
                        numbersSlider.valueProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number initValue, Number newValue) {
                                int currentNumbers = newValue.intValue();
                                System.out.println("Slider " + id + " value changed:" + currentNumbers);
                                indicator.setText(String.valueOf(currentNumbers)); // update indicator numbers
                                sessionConfig.setNumbersSize(currentNumbers);          // update session config numbers
                            }
                        });

                        break;

                    case "configSpeed":
                        // Common slider configs
                        Slider speedSlider = (Slider)configsAnchor.lookup("#" + id);
                        Text inidcator = ((Text)configsAnchor.lookup("#configSpeedIndicator"));

                        // config number slider event listener
                        timeDurationSliderListener(session, speedSlider, inidcator);

                        break;
                }
            } catch ( NullPointerException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void sliderConfig(Slider s, double max){
        s.setShowTickLabels(true);
        s.setShowTickMarks(true);
        s.setMajorTickUnit(max/5f);
        s.setBlockIncrement(max/10f);
    }

    public void sliderConfig(Slider s, long max){
        s.setShowTickLabels(true);
        s.setShowTickMarks(true);
        s.setMajorTickUnit(max/5f);
        s.setBlockIncrement(max/10f);
    }

    public void timeDurationSliderListener(Session session,Slider slider, Text text){
        // Set default value
        sliderConfig(slider, session.getConfig().MAX_SPEED_INTERVAL);
        slider.setMin(session.getConfig().MIN_SPEED_INTERVAL);
        slider.setMax(session.getConfig().MAX_SPEED_INTERVAL);
        slider.setValue(session.getConfig().getSpeedInterval());
        text.setText((session.getConfig().getSpeedInterval()*1000.0)/1000.0 + " millisec");

        slider.valueProperty().addListener((observableValue, initValue, newValue) -> {
            long currentIntervalSpeed = Double.valueOf((Double) newValue).longValue();
            System.out.println("Slider  " + slider.getId() + " value changed:" + currentIntervalSpeed);
            text.setText((currentIntervalSpeed*1000.0)/1000.0 + " millisec"); // update indicator speed interval
            session.getConfig().setSpeedInterval(currentIntervalSpeed);    // update session config speed interval
        });
    }

    /**
     * Set up logged in user split menu button.
     *
     * @return the split menu button
     */
    public SplitMenuButton setUpLoggedInUser(){
        UserModel userModelLoggedIn = Main.userModelLoggedIn;

        Text roleText  = (Text) loggedinUserVBox.lookup("#" + loggedinUserVBox.getChildren().get(0).getId());
        roleText.setText( userModelLoggedIn.getRoles().get(0) );

        SplitMenuButton m  = (SplitMenuButton)loggedinUserVBox.lookup("#" + loggedinUserVBox.getChildren().get(1).getId());
        String fullname = userModelLoggedIn.getFirstname() + " " + userModelLoggedIn.getLastname();
        m.setText(fullname);

        System.out.println(
                "ConfigurationController.setUpLoggedInUser(): " +
                        "\n" + "Fullname: " + fullname  +
                        "\n" + "User ID: " + userModelLoggedIn.getUser_id() +
                        "\n" + "username: " + userModelLoggedIn.getUsername() +
                        "\n" + "Role: " + userModelLoggedIn.getRoles() );

        return m;

    }

    /**
     * Init data.
     */
    public void initData() {

        Main.showImage("assets/img/avatar.png", avatarImageView);
        SplitMenuButton m = setUpLoggedInUser();

        // Create Menu Items programmatically
        MenuItem users =  new MenuItem("Manage Users");
        MenuItem history = new MenuItem("History");
        MenuItem configuration = new MenuItem("Configuration");
        MenuItem logout  = new MenuItem("Logout");
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
        for(int i=0; i< m.getItems().size(); i++){
            int finalI = i;
            m.getItems().get(i).setOnAction((e)->{
                // new alert
                Alert alert ;
                Optional<ButtonType> result = Optional.of(ButtonType.OK);
                if ( Session.getInstace().getThread() != null && Session.getInstace().getThread().isAlive()){
                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Terminate " + Session.getInstace().getThread().getName() );
                    alert.setContentText("Are you sure you want to leave?");
                    result = alert.showAndWait();
                }

                // result action
                if (result.get() == ButtonType.OK){
                    // ... user chose OK
                    stopThread();
                    System.out.println("==============================================");
                    System.out.println("Loading " + btnLoadScenes.get(finalI) + ".fxml");
                    System.out.println("==============================================");
                    Main.loadScene(e,   btnLoadScenes.get(finalI) , true);
                } else {
                    // ... user chose CANCEL or closed the dialog
                    System.out.println("request cancelled");
                }

            });
        }

        // Could use capabilities for more flexibility
        // Restricting admin vs. user view
        if ( !Main.userModelLoggedIn.getRoles().get(0).equals("Administrator") && !Main.userModelLoggedIn.getRoles().get(0).equals("Manager") ){
            users.setVisible(false);
        }

        // add eventListener for fx:id="closeBtn"
        Button closeBtn = (Button)vTop.lookup("#closeBtn");
        closeBtn.setOnAction((e)->{
            System.out.println("Close Btn clicked.");
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.close();
        });

        // add eventListener for profile using the split menu main button
        m.setOnAction((e)->{
            // new alert
            Alert alert ;
            Optional<ButtonType> result = Optional.of(ButtonType.OK);
            if ( Session.getInstace().getThread() != null && Session.getInstace().getThread().isAlive()){
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Terminate " + Session.getInstace().getThread().getName() );
                alert.setContentText("Are you sure you want to leave?");
                result = alert.showAndWait();
            }

            // result action
            if (result.get() == ButtonType.OK){
                // ... user chose OK
                stopThread();
                System.out.println("==============================================");
                System.out.println("Loading " + "profile" + ".fxml");
                System.out.println("==============================================");
                Main.loadScene(e,   "profile" , true);
            } else {
                // ... user chose CANCEL or closed the dialog
                System.out.println("request cancelled");
            }
        });



    }

    private void stopThread(){
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


}
