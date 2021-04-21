package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import models.DaoModel;
import models.User;

import java.io.File;
import java.io.IOException;


public class Main extends Application {

    Stage window;


    private static double xOffset = 0;
    private static double yOffset = 0;
    public static User userLoggedIn;

    @FXML
    public static SplitMenuButton splitMenuButton;


    // Main
    public static void main(String[] args) {
        DaoModel dao = new DaoModel();
        dao.createTable();
        dao.setupRoot();
        launch(args);
    }

    @FXML
    public static void showImage(String pathname, ImageView imageView) {
        System.out.println("main.showImage(): Setting Branding Image View");
        try {
            File brandingImageFile = new File(pathname);
            Image image = new Image(brandingImageFile.toURI().toString());
            imageView.setImage(image);
            imageView.setCache(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  JavaFX
    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(root);

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                window.setX(event.getScreenX() - xOffset);
                window.setY(event.getScreenY() - yOffset);
            }
        });

        window.setScene(scene);
        window.initStyle(StageStyle.UNDECORATED);

        window.setTitle("Algorithm Visualizer");
        window.show();

    }

    public static void logout(Button fxmlBtn){
        Button logout =  new Button();
        logout.setOnAction((e)->{
            Main.loadScene(e, "login", false);
        });

        Stage stage = (Stage)fxmlBtn.getScene().getWindow();
        StackPane layout= new StackPane();
        layout.getChildren().add(logout);
        Scene scene1= new Scene(layout);
        stage.setScene(scene1);
        logout.fire();
    }

    public static Object loadScene(ActionEvent evt, String viewFilename, boolean isMenu){
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/" + viewFilename + ".fxml"));
        try{
            Stage stage;
            if ( evt.getTarget().getClass().toString().contains("SplitMenuButton")){
                // profile button
                stage = (Stage) (((SplitMenuButton)evt.getTarget()).getScene().getWindow());
            } else {
                stage = isMenu
                        ? (Stage) ((MenuItem)evt.getTarget()).getParentPopup().getOwnerWindow() // menu button
                        : (Stage) ((Node)evt.getSource()).getScene().getWindow(); // regular call
            }

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            makeDraggable(scene, stage);
        } catch (IOException e){
            e.printStackTrace();
        }

        return  loader.getController();
    }

    public static void makeDraggable(Scene root, Stage stage){
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
    }
}
