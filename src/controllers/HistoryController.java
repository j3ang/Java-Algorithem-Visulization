package controllers;

import application.Main;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class HistoryController  extends ConfigurationController implements Initializable {

    @FXML
    private ImageView avatarImageView;
    @FXML
    private SplitMenuButton splitMenuButton;
    @FXML
    private VBox loggedinUserVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
    }


    public void historyBackBtnAction(ActionEvent evt) {
        Main.loadScene(evt, "configuration", false);
    }


}
