package controllers;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import models.DaoModel;
import models.UserModel;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The type Profile controller.
 */
public class ProfileController extends UsersController implements Initializable {


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


    /**
     * Fill input box.
     */
    public void fillInputBox(){
        for (int i=0; i< usersVboxInput.getChildren().size(); i++){
            TextField tf = (TextField)usersVboxInput.lookup("#" + usersVboxInput.getChildren().get(i).getId());
            switch (i){
                case 0:
                    tf.setText(String.valueOf(Main.userModelLoggedIn.getUser_id()));
                    break;
                case 1:
                    tf.setText(Main.userModelLoggedIn.getFirstname());
                    break;
                case 2:
                    tf.setText(Main.userModelLoggedIn.getLastname());
                    break;
                case 3:
                    tf.setText(Main.userModelLoggedIn.getUsername());
                    tf.setDisable(true);
                    break;
                case 4:
                    tf.setText(Main.userModelLoggedIn.getPassword());
                    break;
            }
        }
    }

    @FXML
    private void BtnUpdateUserAction(ActionEvent event){
        System.out.println("update button in profile controller clicked.");

        try{
            UserModel updateUserModel = new UserModel();
            boolean pwChanged = false;
            for (int i=0; i< usersVboxInput.getChildren().size(); i++){
                TextField tf = (TextField)usersVboxInput.lookup("#" + usersVboxInput.getChildren().get(i).getId());
                switch (i){
                    case 0:
                        updateUserModel.setUser_id(Integer.parseInt(tf.getText().strip()));
                        break;
                    case 1:
                        updateUserModel.setFirstname(tf.getText().strip());
                        break;
                    case 2:
                        updateUserModel.setLastname(tf.getText().strip());
                        break;
                    case 3:
                        updateUserModel.setUsername(tf.getText().strip());
                        break;
                    case 4:
                        String currTfPwHashed  = SignupController.hashPassword(tf.getText().strip());
                        // incase use has accidentally changed the password
                        if (  !Main.userModelLoggedIn.getPassword().equals(tf.getText().strip()) ){

                            System.out.println("Main logedin user paassword: " + Main.userModelLoggedIn.getPassword());
                            System.out.println("Textfield password: " + tf.getText().strip());
                            System.out.println("password changed");

                            updateUserModel.setPassword(currTfPwHashed);
                            pwChanged  = true;

                        } else{
                            updateUserModel.setPassword(tf.getText().strip());
                        }

                        break;
                }
            }

            updateUserModel.save(dao, true, false);
            updateUserModel.setRoles(Main.userModelLoggedIn.getRoles());
            Main.userModelLoggedIn = updateUserModel; // update static logged in user
            new ConfigurationController().setUpLoggedInUser(); // refresh loggedIn user

            // if password has changed, logout
            if ( pwChanged ){
                Main.logout(usersBtnUpdate);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }


}
