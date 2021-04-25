package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class HboxController implements Initializable {

    @FXML
    private final StackPane stackPane = new StackPane();
    @FXML
    private HBox rectWraper = new HBox();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        stackPane.getChildren().add(rectWraper);
    }

    public HBox getRectWraper() {
        return rectWraper;
    }

    public HboxController setRectWraper(HBox rectWraper) {
        this.rectWraper = rectWraper;
        return this;
    }
}
