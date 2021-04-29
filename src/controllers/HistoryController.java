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


/**
 * The type History controller.
 */
public class HistoryController  extends ConfigurationController implements Initializable {


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
    }



    /**
     * History back btn action.
     *
     * @param evt the evt
     */
    public void historyBackBtnAction(ActionEvent evt) {
        Main.loadScene(evt, "configuration", false);
    }


}
