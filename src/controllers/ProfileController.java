package controllers;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.DaoModel;
import models.UserModel;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The type Profile controller.
 */
public class ProfileController extends UsersController implements Initializable {


    @FXML
    private VBox usersVboxInput;

    @FXML
	private VBox loggedinUserVBox;

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
                if( i!=3 && usersVboxInput.getChildren().get(i) instanceof TextField){
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

			if (usersVboxInput.getChildren().get(i) instanceof TextField) {
				TextField tf = (TextField) usersVboxInput.lookup("#" + usersVboxInput.getChildren().get(i).getId());
				switch (i){
					case 0:
						tf.setText(String.valueOf(Main.userModelLoggedIn.getUser_id()).strip());
						break;
					case 1:
						tf.setText(Main.userModelLoggedIn.getFirstname().strip());
						break;
					case 2:
						tf.setText(Main.userModelLoggedIn.getLastname().strip());
						break;
					case 3:
						System.out.println("Mainuser logged in username: " + Main.userModelLoggedIn.getUsername() );
						tf.setText(Main.userModelLoggedIn.getUsername().strip());
						tf.setDisable(true);
						break;
					case 4:
						tf.setText(Main.userModelLoggedIn.getPassword().strip());
						break;
				}
			}
        }
    }

    @FXML
    private void BtnUpdateUserAction(ActionEvent event){
        System.out.println("update button in profile controller clicked.");

        try{
            UserModel updateUserModel = new UserModel();
            boolean pwChanged = false;
				for (int i = 0; i < usersVboxInput.getChildren().size(); i++) {
					if (usersVboxInput.getChildren().get(i) instanceof TextField) {
					TextField tf = (TextField) usersVboxInput.lookup("#" + usersVboxInput.getChildren().get(i).getId());
						System.out.println(i +  " : " + tf.getText().strip());
					switch (i) {
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
							String currTfPwHashed = SignupController.hashPassword(tf.getText().strip());
							// in case use has accidentally changed the password
							if (!Main.userModelLoggedIn.getPassword().strip().equals(tf.getText().strip())) {

								System.out.println("Main logedin user paassword: " + Main.userModelLoggedIn.getPassword().strip());
								System.out.println("Textfield password: " + tf.getText().strip());
								System.out.println("password changed");

								updateUserModel.setPassword(currTfPwHashed);
								pwChanged = true;

							} else {
								updateUserModel.setPassword(tf.getText().strip());
							}

							break;
					}
				}
			}

				// update
            	updateUserModel.save(dao, true, false);
            updateUserModel.setRoles(Main.userModelLoggedIn.getRoles());
            Main.userModelLoggedIn = updateUserModel; // update static logged in user

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText("Updated successful");
			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == ButtonType.OK){
				// ... user chose OK
				System.out.println("\n===================================");
				System.out.println("your info has updated successfully.");
				System.out.println("===================================\n");
				setUpLoggedInUser(); // refresh dropdown menu

				// if password has changed, logout
				if ( pwChanged ){
					Main.logout(usersBtnUpdate);
				}

			} else {
				// ... user chose CANCEL or closed the dialog
				System.out.println("request cancelled");
			}

		} catch (Exception e){
            e.printStackTrace();
        }

    }

    // override configuration setUpLoggedInUser() method
	public SplitMenuButton setUpLoggedInUser() {
		UserModel userModelLoggedIn = Main.userModelLoggedIn;
		Text roleText = (Text) loggedinUserVBox.lookup("#" + loggedinUserVBox.getChildren().get(0).getId());
		roleText.setText(userModelLoggedIn.getRoles().get(0));

		SplitMenuButton m = (SplitMenuButton) loggedinUserVBox.lookup("#" + loggedinUserVBox.getChildren().get(1).getId());
		String fullname = userModelLoggedIn.getFirstname() + " " + userModelLoggedIn.getLastname();
		m.setText(fullname);
		return m;
	}

}
