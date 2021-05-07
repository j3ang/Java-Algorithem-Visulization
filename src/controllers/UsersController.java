package controllers;

import application.Main;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.*;

import java.awt.desktop.UserSessionEvent;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Users controller.
 */
public class UsersController extends ConfigurationController implements Initializable {

    // Users table view
    @FXML
    private TableView usersTable;

    // Form Input TextFields
    @FXML
    private TextField usersInputUserId;
    @FXML
    private TextField usersInputFirstname;
    @FXML
    private TextField usersInputLastname;
    @FXML
    private TextField usersInputUsername;
    @FXML
    private TextField usersInputPassword;

    // ComboBox
    @FXML
    private ComboBox usersComboRole;

    // Table Column Fields
    @FXML
    private TableColumn<UserModel, Integer> usersColUserId;
    @FXML
    private TableColumn<UserModel, String> usersColFirstname;
    @FXML
    private TableColumn<UserModel, String> usersColLastname;
    @FXML
    private TableColumn<UserModel, String> usersColUsername;
    @FXML
    private TableColumn<UserModel, String> usersColPassword;
    @FXML
    private TableColumn<UserModel, String> usersColRole;

    // CRUD Buttons
    @FXML
    private Button usersBtnNewUser;
    @FXML
    private Button usersBtnUpdate;
    @FXML
    private Button usersBtnDelete;
    @FXML
    private Button usersBtnClear;

    @FXML
    private Button closeBtn;

    // vbox
    @FXML
    private VBox usersVboxInput;

    @FXML
    private VBox loggedinUserVBox;

    @FXML
    private Text usersMessage;

    /**
     * The Dao.
     */
    DaoModel dao;


    public UsersController() {
       super();
       this.dao = new DaoModel();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initData();
        showUsers();
        showRoles();
        bindUpdateBtn(usersBtnUpdate);
        bindUpdateBtn(usersBtnDelete); // delete button has same bindings
        bindNewBtn(usersBtnNewUser);
    }

    /**
     * Bind update btn.
     *
     * @param usersBtnUpdate the users btn update
     */
    public void bindUpdateBtn(Button usersBtnUpdate){

        // Bind text fields states to the new update user button
        usersBtnUpdate.disableProperty().bind(
                Bindings.isEmpty(usersInputUserId.textProperty())
                        .or(Bindings.isEmpty(usersInputFirstname.textProperty()))
                        .or(Bindings.isEmpty(usersInputLastname.textProperty()))
                        .or(Bindings.isEmpty(usersInputUsername.textProperty()))
                        .or(Bindings.isEmpty(usersInputPassword.textProperty()))
        );
    }

    /**
     * Bind new btn.
     *
     * @param usersBtnNewUser the users btn new user
     */
    public void bindNewBtn(Button usersBtnNewUser){
        // Bind text fields states to the new user button
        usersBtnNewUser.disableProperty().bind(
                Bindings.isNotEmpty(usersInputUserId.textProperty())
                        .or(Bindings.isEmpty(usersInputFirstname.textProperty()))
                        .or(Bindings.isEmpty(usersInputLastname.textProperty()))
                        .or(Bindings.isEmpty(usersInputUsername.textProperty()))
                        .or(Bindings.isEmpty(usersInputPassword.textProperty()))
        );
    }


    /**
     * Get users list observable list.
     *
     * @return the observable list
     */
    public ObservableList<UserModel> getUsersList(){
        ObservableList<UserModel> usersList = FXCollections.observableArrayList();
        UserModel userModel =  new UserModel();
        Vector users = userModel.getAllUsers(dao);
        Vector userRoles = userModel.getAllUserRoles(dao);

        HashMap<Integer, String> roleMap = new HashMap<>();
        userRoles.forEach(role->{
            String[] parsedMeta = role.toString().replaceAll("\\p{P}","").split(" ");
            int parsedId =  Integer.valueOf(parsedMeta[1]);
            String parsedRole = parsedMeta[3];
            roleMap.put(parsedId, parsedRole);
        });

        for (int i = 0; i < users.size(); i++){
            // parse user
            String[] parsedUser = users.get(i).toString()
                    .replaceAll("\\[", "")
                    .replaceAll("\\]","")
                    .split(",");
            // Create new User Object
            UserModel user = UserModel.vectorToUser(parsedUser);
            System.out.println("user id: " + user.getUser_id());
            // if user id match -> set user role
            if ( roleMap.containsKey(user.getUser_id()) ){
                ArrayList<String> userRole = new ArrayList<>();
                userRole.add( roleMap.get(user.getUser_id()));
                user.setRoles(userRole);
            }
            usersList.add(user); // Populate the user Observable list
        }
        return usersList;
    }

    /**
     * Get role caps list observable list.
     *
     * @return the observable list
     */
    public ObservableList<RoleModel> getRoleCapsList(){
        ObservableList<RoleModel> optionList = FXCollections.observableArrayList();
        OptionModel optionModel = new OptionModel();

        // option role_capabilities is a vector of role_cap key pairs
        Vector<RoleModel> role_caps = optionModel.getRoleCapOption("role_capabilities");
        for ( int i = 0 ; i < role_caps.size(); i++ ){
            optionList.add(role_caps.get(i));
        }

        System.out.println("UsersController.getRoleCapsList(): " + optionList);
        return optionList;

    }


    /**
     * Show users.
     */
    public void showUsers(){

        ObservableList<UserModel> userModels = getUsersList();

        usersColUserId.setCellValueFactory(new PropertyValueFactory<UserModel, Integer>("user_id"));
        usersColFirstname.setCellValueFactory(new PropertyValueFactory<UserModel, String>("firstname"));
        usersColLastname.setCellValueFactory(new PropertyValueFactory<UserModel, String>("lastname"));
        usersColUsername.setCellValueFactory(new PropertyValueFactory<UserModel, String>("username"));
        usersColPassword.setCellValueFactory(new PropertyValueFactory<UserModel, String>("password"));
        usersColRole.setCellValueFactory(new PropertyValueFactory<UserModel, String>("roles"));

        usersTable.setItems(userModels);
        System.out.println("UsersController.showUsers()");
    }

    /**
     * Show roles.
     */
    public void showRoles(){
        ObservableList<RoleModel> roleList = getRoleCapsList();
        ObservableList<String> roleNameList =  FXCollections.observableArrayList();

        for ( int i=0; i< roleList.size(); i++ ){
            roleNameList.add(roleList.get(i).getRole());
        }

        usersComboRole.setItems(roleNameList);
        System.out.println("getRoleCapsList.showRoles()");

    }


    @FXML
    private void BtnClearAction(ActionEvent event){
        // Clear fields
        Parent parent = usersBtnClear.getParent();
        for (int i=0; i< usersVboxInput.getChildren().size()-2; i++ ){
            TextField textField = (TextField) parent.lookup("#" + usersVboxInput.getChildren().get(i).getId());
            textField.setText("");
        }
    }

    @FXML
    private void BtnNewUserAction(ActionEvent event){
        usersMessage.setText(""); // clear previous messages
        // save new user of not exist in users table
        if ( ! dao.rowExists(dao.getTableName("users"), "username", usersInputUsername.getText()) ){
            ArrayList<String> roles = new ArrayList<>();
            roles.add(usersComboRole.getValue().toString());

            UserModel userModel = new UserModel(
                    usersInputFirstname.getText(),
                    usersInputLastname.getText(),
                    usersInputUsername.getText(),
                    SignupController.hashPassword(usersInputPassword.getText()),
                    roles);
            userModel.save(dao, false, true);

            UserModel userModelMetaSaved = new UserModel( UserModel.getUserByUsername(dao, userModel.getUsername()).getUser_id(), roles );
            UserModel.setUserRole(dao, userModelMetaSaved);

            showUsers(); // update table list view
        } else {
            usersMessage.setText("Username: " + usersInputUsername.getText() + " is taken.");
        }


    }

    @FXML
    private void BtnUpdateUserAction(ActionEvent event){
        UserModel userModel = (UserModel) usersTable.getSelectionModel().getSelectedItem();

        userModel.setUser_id(Integer.parseInt(usersInputUserId.getText().strip()));
        userModel.setFirstname(usersInputFirstname.getText().strip());
        userModel.setLastname(usersInputLastname.getText().strip());
        userModel.setUsername(usersInputUsername.getText().strip());
        System.out.println("current user input password: " + usersInputPassword.getText().strip());
        System.out.println("user object password: " + userModel.getPassword().strip());
        if ( ! usersInputPassword.getText().strip().equals(userModel.getPassword().strip())){
            System.out.println("password changed");
            System.out.println(" hashing new password: " + SignupController.hashPassword(usersInputPassword.getText().strip()));
            userModel.setPassword(SignupController.hashPassword(usersInputPassword.getText().strip()));
        } else {
            userModel.setPassword(usersInputPassword.getText().strip());
        }


        userModel.save(dao, true, false); // Update

        // User meta roles
        ArrayList<String> userRoles = new ArrayList<>();
        userRoles.add(usersComboRole.getValue().toString());
        UserModel userModelMetaSaved = new UserModel(new UserModel().getUserByUsername(dao, userModel.getUsername()).getUser_id(), userRoles);
        new UserModel().setUserRole(dao, userModelMetaSaved);

        // logout if downgrading current user from admin to user
        if (usersComboRole.getValue().toString().contains("User") && Main.userModelLoggedIn.getUser_id() == Integer.parseInt(usersInputUserId.getText().strip()) ){
            System.out.println("Downgraded to user role");
            System.out.println("Logging out now...");
            Main.logout(usersBtnUpdate); // pass in a btn from this scene to get current window
        } else {
            showUsers(); // Refresh
        }


    }

    @FXML
    private void BtnDeleteUserAction(ActionEvent event){

        if( confirmAction("delete this user?") ){
            UserModel userModel = (UserModel) usersTable.getSelectionModel().getSelectedItem();
            userModel.delete(dao);
            usersBtnClear.fire(); // clear form
            showUsers(); // refresh users list
        }

    }

    @FXML
    private boolean confirmAction(String action){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to " + action);
        UserModel userModel = (UserModel) usersTable.getSelectionModel().getSelectedItem();
        alert.setContentText(
                        "User ID: " + userModel.getUser_id() + "\n" +
                        "Username: " + userModel.getUsername() + "\n" +
                        "Firstname: " + userModel.getFirstname() + "\n" +
                        "Lastname: " + userModel.getLastname() + "\n"
        );

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // ... user chose OK
            System.out.println("ok to commit");
            return true;
        } else {
            // ... user chose CANCEL or closed the dialog
            System.out.println("request cancelled");
            return false;

        }

    }

    /**
     * Btn back action.
     *
     * @param evt the evt
     */
    @FXML
    public void BtnBackAction(ActionEvent evt) {
        Main.loadScene(evt, "configuration", false);
    }

    @FXML
    private void tableViewClickedAction(MouseEvent event){
        try{
            UserModel userModel = (UserModel) usersTable.getSelectionModel().getSelectedItem();
            // Populate the form inout TextFields
            usersInputUserId.setText(String.valueOf(userModel.getUser_id()).strip());
            usersInputUserId.setEditable(false); // Disable user_id field
            usersInputFirstname.setText(userModel.getFirstname().strip());
            usersInputLastname.setText(userModel.getLastname().strip());
            usersInputUsername.setText(userModel.getUsername().strip());
            usersInputPassword.setText(userModel.getPassword().strip());
            usersComboRole.setValue(userModel.getRoles().get(0));
        } catch ( NullPointerException e){
            System.out.println("Not users in clicked cell.");
            e.printStackTrace();
        }

    }

}
