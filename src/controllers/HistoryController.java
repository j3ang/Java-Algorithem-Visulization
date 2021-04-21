package controllers;

import application.Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

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
