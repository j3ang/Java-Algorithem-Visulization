package controllers;

import application.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.ConfigModel;
import models.Session;
import models.UserModel;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class ConfigurationController implements Initializable  {
    @FXML
    private ImageView avatarImageView;
    @FXML
    private VBox loggedinUserVBox;
    @FXML
    private VBox vTop;
    @FXML
    private AnchorPane configsAnchor;

    ConfigModel sessionConfig;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
        Session  session = Session.getInstace(new ConfigModel());
        sessionConfig = session.getConfig();
        System.out.println(session);
        setUpParams(sessionConfig);

    }

    private void setUpParams(ConfigModel config) {
        for ( int i = 0; i <  configsAnchor.getChildren().size(); i++ ){
            String id = configsAnchor.getChildren().get(i).getId();
            try {
                switch (id) {
                    case "configNumbers":
                        // Common slider configs
                        Slider numbersSlider = (Slider)configsAnchor.lookup("#" + id);
                        sliderConfig(numbersSlider, (double) config.MAX_NUMBERS);
                        numbersSlider.setMin(config.MIN_NUMBERS);
                        numbersSlider.setMax(config.MAX_NUMBERS);

                        // Set default value
                        Text indicator = ((Text)configsAnchor.lookup("#configNumbersIndicator"));
                        indicator.setText(String.valueOf(config.getNumbersSize()));
                        numbersSlider.setValue(config.getNumbersSize());


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
                        sliderConfig(speedSlider, config.MAX_SPEED_INTERVAL);
                        speedSlider.setMin(config.MIN_SPEED_INTERVAL);
                        speedSlider.setMax(config.MAX_SPEED_INTERVAL);

                        // Set default value
                        speedSlider.setValue(config.getSpeedInterval());
                        Text inidcator = ((Text)configsAnchor.lookup("#configSpeedIndicator"));
                        inidcator.setText(String.valueOf(Math.round(config.getSpeedInterval() * 1000.0)/1000.0));

                        // config number slider event listener
                        speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number initValue, Number newValue) {
                                double currentIntervalSpeed = Math.round(newValue.doubleValue() * 1000.0)/1000.0;
                                System.out.println("Slider " + id + " value changed:" + currentIntervalSpeed);
                                inidcator.setText(String.valueOf(currentIntervalSpeed)); // update indicator speed interval
                                sessionConfig.setSpeedInterval(currentIntervalSpeed);    // update session config speed interval
                            }
                        });

                        break;
                }
            } catch ( NullPointerException e){
                System.out.println(e.getMessage());
            }


        }
    }

    public void sliderConfig(Slider s, Double max){
        s.setShowTickLabels(true);
        s.setShowTickMarks(true);
        s.setMajorTickUnit(max/5f);
        s.setBlockIncrement(max/10f);
    }

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
                System.out.println("==============================================");
                System.out.println("Loading " + btnLoadScenes.get(finalI) + ".fxml");
                System.out.println("==============================================");
                Main.loadScene(e,   btnLoadScenes.get(finalI) , true);
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
            Main.loadScene(e, "profile", true);
        });






    }

    public void runAction(ActionEvent evt) throws IOException {
        MainController m = (MainController) Main.loadScene(evt, "main", false);

//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
//        Parent root = (Parent)loader.load();
//        MainController controller = (MainController)loader.getController();
//        Button btn = new Button();
//       Stage stage = new Stage();
//        StackPane stackPane = new StackPane();
//        stackPane.getChildren().add(btn);
//        stage.setScene(new Scene(stackPane, 300, 250));
//        controller.setStage(stage);
//        controller.init();

    }


}
