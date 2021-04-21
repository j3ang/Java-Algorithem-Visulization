package controllers;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import models.User;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController extends  UsersController implements Initializable {


    @FXML
    private VBox usersVboxInput;
    @FXML
    private Button usersBtnUpdate;

    @FXML
    private Button usersBtnClear;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
        fillInputBox();
        bindUpdateBtn(usersBtnUpdate);

        usersBtnClear.setOnAction((e)->{
            for (int i = 1 ; i < usersVboxInput.getChildren().size()-1; i++){
                if( i!=3){
                    TextField tf = (TextField)usersVboxInput.lookup("#" + usersVboxInput.getChildren().get(i).getId());
                    tf.setText("");
                }
            }
        });

    }


    public void fillInputBox(){
        for (int i=0; i< usersVboxInput.getChildren().size(); i++){
            TextField tf = (TextField)usersVboxInput.lookup("#" + usersVboxInput.getChildren().get(i).getId());
            switch (i){
                case 0:
                    tf.setText(String.valueOf(Main.userLoggedIn.getUser_id()));
                    break;
                case 1:
                    tf.setText(Main.userLoggedIn.getFirstname());
                    break;
                case 2:
                    tf.setText(Main.userLoggedIn.getLastname());
                    break;
                case 3:
                    tf.setText(Main.userLoggedIn.getUsername());
                    tf.setDisable(true);
                    break;
                case 4:
                    tf.setText(Main.userLoggedIn.getPassword());
                    break;
            }
        }
    }

    @FXML
    private void BtnUpdateUserAction(ActionEvent event){
        System.out.println("update button in profile controller clicked.");

        try{
            User updateUser = new User();
            boolean pwChanged = false;
            for (int i=0; i< usersVboxInput.getChildren().size(); i++){
                TextField tf = (TextField)usersVboxInput.lookup("#" + usersVboxInput.getChildren().get(i).getId());
                switch (i){
                    case 0:
                        updateUser.setUser_id(Integer.parseInt(tf.getText().strip()));
                        break;
                    case 1:
                        updateUser.setFirstname(tf.getText().strip());
                        break;
                    case 2:
                        updateUser.setLastname(tf.getText().strip());
                        break;
                    case 3:
                        updateUser.setUsername(tf.getText().strip());
                        break;
                    case 4:
                        String currTfPwHashed  = SignupController.hashPassword(tf.getText().strip());
                        // incase use has accidentally changed the password
                        if (  !Main.userLoggedIn.getPassword().equals(tf.getText().strip()) ){

                            System.out.println("Main logedin user paassword: " + Main.userLoggedIn.getPassword());
                            System.out.println("Textfield password: " + tf.getText().strip());
                            System.out.println("password changed");

                            updateUser.setPassword(currTfPwHashed);
                            pwChanged  = true;

                        } else{
                            updateUser.setPassword(tf.getText().strip());
                        }

                        break;
                }
            }

            updateUser.save(true, false);
            updateUser.setRoles(Main.userLoggedIn.getRoles());
            Main.userLoggedIn = updateUser; // update static logged in user
            setUpLoggedInUser(); // refresh loggedIn user

            // if password has changed, logout
            if ( pwChanged ){
                Main.logout(usersBtnUpdate);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }


}
