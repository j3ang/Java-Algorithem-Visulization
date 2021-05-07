package controllers;


import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.DaoModel;
import models.LoginModel;
import models.UserModel;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The type Login controller.
 */
public class LoginController extends DaoModel implements Initializable {

    @FXML
    private ImageView brandingImageView;
    @FXML
    private Label loginMessage;
    @FXML
    private Button closeBtn;
    @FXML
    private Button btnSignUp;

    @FXML
    private TextField inputUsername;
    @FXML
    private TextField inputPassword;

    private Scene signupScene;
    private Scene configurationScene;



    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.showImage("assets/img/algoViz_login.jpg", brandingImageView);
    }

    /**
     * Login btn on action.
     *
     * @param evt the evt
     */
    public void loginBtnOnAction(ActionEvent evt){
        if ( inputUsername.getText().isBlank() == false && inputPassword.getText().isBlank() == false  ){
            loginMessage.setText("Logging in...");

            try{
                LoginModel loginModel = new LoginModel();

                if ( loginModel.login(
                        inputUsername.getText(),
                        SignupController.hashPassword( inputPassword.getText() ) )
                ){
                    Main.userModelLoggedIn = new UserModel().getUserByUsername(this, inputUsername.getText());
                    nextScreen(evt);


                }
                else
                    loginMessage.setText("Credentials not found in database, try again...");

            } catch (Exception e){
                e.printStackTrace();
            }

        } else {
            loginMessage.setText("Please enter username and password.");
        }

    }


    /**
     * Signup btn on action.
     *
     * @param evt the evt
     */
    public void signupBtnOnAction(ActionEvent evt){
        Main.loadScene(evt, "signup", false);
    }


    /**
     * Next screen.
     *
     * @param evt the evt
     */
    public void nextScreen(ActionEvent evt){
        Main.loadScene(evt, "configuration", false);
    }


    public void closeBtnOnAction(ActionEvent evt){
		System.out.println("Close Btn clicked.");
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
		stage.close();
	}
}
