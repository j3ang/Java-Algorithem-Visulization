package controllers;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import models.DaoModel;
import models.UserModel;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * The type Signup controller.
 */
public class SignupController implements Initializable  {

    private Scene loginScene;

    @FXML
    private ImageView brandingImageView;
    @FXML
    private Label signupMessage;

    private final HashMap<String,  Object> form = new HashMap<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.showImage("/assets/img/algoViz_login.jpg", brandingImageView);
    }

    /**
     * Create acct.
     */
    public void createAcct(){
        String[] formItems = {"Firstname", "Lastname", "Username", "Password"};

        // Get input
       for( String tf : formItems){
           Parent parent = brandingImageView.getParent(); // the Parent (or Scene) that contains the TextFields
           TextField textField = (TextField) parent.lookup("#signupInput" + tf);
           String val = textField.getText().trim();
           // if the field exist and
           if (textField != null && !val.isEmpty()){
               String key = textField.getPromptText();
               String value = val;
               if (key.equals("Password")){
                   value = hashPassword(val);
               }
               form.put(key, value);
           }
       }

       // process & validate data
       Boolean isValid = validateForm(formItems);

        // Save to database
       if (isValid){

           DaoModel dao = new DaoModel();

           UserModel newUserModel = new UserModel(
                   form.get("Firstname").toString(),
                   form.get("Lastname").toString(),
                   form.get("Username").toString(),
                   form.get("Password").toString());
           newUserModel.save(dao, false, true);

           signupMessage.setText("Your account has been created.");
           Parent parent = brandingImageView.getParent(); // the Parent (or Scene) that contains the TextFields
           Button cancelBtn = (Button) parent.lookup("#signupBtnCancel");
           cancelBtn.setText("Back");
       }

    }


    /**
     * Signup btn cancel on action.
     *
     * @param evt the evt
     */
    @FXML
    public void signupBtnCancelOnAction(ActionEvent evt){
        Main.loadScene(evt, "login", false);
    }

    /**
     * Validate form boolean.
     *
     * @param formItems the form items
     * @return the boolean
     */
    public Boolean validateForm(String[] formItems){

        // Check if account already exists
        System.out.println("Validating form: " + form);

        // Message feedback
        if (form.entrySet().size() == formItems.length){
            signupMessage.setText("Creating account...");
            return true;
        } else {
            signupMessage.setText("All fields are required.");
        }

        return false;


    }

    /**
     * Hash password string.
     *
     * @param password the password
     * @return the string
     */
    public static String hashPassword(String password){
        // Hashing password with md5 since it's wildly supported
        String md5Hex = DigestUtils.md5Hex(password);
        return md5Hex;
    }


}
