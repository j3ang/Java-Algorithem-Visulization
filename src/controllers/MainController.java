package controllers;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends ConfigurationController implements Initializable {

    @FXML
    private ImageView avatarImageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.showImage("assets/img/avatar.png", avatarImageView);
    }


    public void mainStartNewBtnOnAction(ActionEvent evt) {
        Main.loadScene(evt, "configuration", false);
    }

    public void mainRestartBtnOnAction(ActionEvent actionEvent) {
        System.out.println("main restart btn clicked");
    }

    public void mainPauseBtnOnAction(ActionEvent actionEvent) {
        System.out.println("main paused btn clicked");
    }
}
