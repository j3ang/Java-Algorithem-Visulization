package controllers;

import application.Main;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.*;

import java.net.URL;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.sql.ResultSet;

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
    private TableColumn<User, Integer> usersColUserId;
    @FXML
    private TableColumn<User, String> usersColFirstname;
    @FXML
    private TableColumn<User, String> usersColLastname;
    @FXML
    private TableColumn<User, String> usersColUsername;
    @FXML
    private TableColumn<User, String> usersColPassword;
    @FXML
    private TableColumn<User, String> usersColRole;

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



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
        showUsers();
        showRoles();
        bindUpdateBtn(usersBtnUpdate);
        bindNewBtn(usersBtnNewUser);
    }

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

    public ObservableList<User> getUsersList(){
        ObservableList<User> usersList = FXCollections.observableArrayList();
        Vector users = User.getAllUsers();
        DaoModel dao = new DaoModel();

        // Create a new user per each result set row
        for(int i = 0 ; i < users.size() ; i++){
            String[] parsedUser = users.get(i).toString()
                    .replaceAll("\\[", "")
                    .replaceAll("\\]","")
                    .split(",");

            User user = User.vectorToUser(parsedUser);


            try{ // Check if this user has role assigned
                ArrayList<String> thisUserRoles = new ArrayList<>();
                String sql = "SELECT meta_desc from " + dao.getTableName("usermeta") +
                    " where user_id="+ user.getUser_id() +" and meta_name='user_roles'";
                ResultSet rs = new DbConnect().connect().createStatement().executeQuery(sql);
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();

                if (rs.next()) { // add to the Observable array list
                    for (int j = 1; j <= columnsNumber; j++) {
                        if (j > 1) System.out.print(",  ");
                        String columnValue = rs.getString(j).replaceAll("\\p{Punct}", "");
                        thisUserRoles.add(columnValue);
                        user.setRoles(thisUserRoles);
                    }
                } else {
                    thisUserRoles.add("User");
                    user.setRoles(thisUserRoles);
                }

                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            usersList.add(user); // Populate the user Observable list
        }
        return usersList;
    }

    public ObservableList<Role> getRoleCapsList(){
        ObservableList<Role> optionList = FXCollections.observableArrayList();
        Option optionModel = new Option();

        // option role_capabilities is a vector of role_cap key pairs
        Vector<Role> role_caps = optionModel.getRoleCapOption("role_capabilities");
        for ( int i = 0 ; i < role_caps.size(); i++ ){
            optionList.add(role_caps.get(i));
        }

        System.out.println("UsersController.getRoleCapsList(): " + optionList);
        return optionList;

    }



    public void showUsers(){

        ObservableList<User> users = getUsersList();

        usersColUserId.setCellValueFactory(new PropertyValueFactory<User, Integer>("user_id"));
        usersColFirstname.setCellValueFactory(new PropertyValueFactory<User, String>("firstname"));
        usersColLastname.setCellValueFactory(new PropertyValueFactory<User, String>("lastname"));
        usersColUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        usersColPassword.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        usersColRole.setCellValueFactory(new PropertyValueFactory<User, String>("roles"));

        usersTable.setItems(users);
        System.out.println("UsersController.showUsers()");
    }

    public void showRoles(){
        ObservableList<Role> roleList = getRoleCapsList();
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
        DaoModel dao = new DaoModel();
        // save new user of not exist in users table
        if ( ! dao.rowExists(dao.getTableName("users"), "username", usersInputUsername.getText()) ){
            ArrayList<String> roles = new ArrayList<>();
            roles.add(usersComboRole.getValue().toString());

            User user = new User(
                    usersInputFirstname.getText(),
                    usersInputLastname.getText(),
                    usersInputUsername.getText(),
                    SignupController.hashPassword(usersInputPassword.getText()),
                    roles);
            user.save(false, true);

            User userMetaSaved = new User( User.getUserByUsername(user.getUsername()).getUser_id(), roles );
            User.setUserRole(dao, userMetaSaved);

            showUsers(); // update table list view
        } else {
            usersMessage.setText("Username: " + usersInputUsername.getText() + " is taken.");
        }


    }

    @FXML
    private void BtnUpdateUserAction(ActionEvent event){
        DaoModel dao = new DaoModel();
        User user = (User) usersTable.getSelectionModel().getSelectedItem();

        user.setUser_id(Integer.parseInt(usersInputUserId.getText().strip()));
        user.setFirstname(usersInputFirstname.getText().strip());
        user.setLastname(usersInputLastname.getText().strip());
        user.setUsername(usersInputUsername.getText().strip());
        System.out.println("current user input password: " + usersInputPassword.getText().strip());
        System.out.println("user object password: " + user.getPassword().strip());
        if ( ! usersInputPassword.getText().strip().equals(user.getPassword().strip())){
            System.out.println("password changed");
            System.out.println(" hashing new password: " + SignupController.hashPassword(usersInputPassword.getText().strip()));
            user.setPassword(SignupController.hashPassword(usersInputPassword.getText().strip()));
        } else {
            user.setPassword(usersInputPassword.getText().strip());
        }


        user.save(true, false); // Update

        // User meta roles
        ArrayList<String> userRoles = new ArrayList<>();
        userRoles.add(usersComboRole.getValue().toString());
        User userMetaSaved = new User(new User().getUserByUsername(user.getUsername()).getUser_id(), userRoles);
        new User().setUserRole(dao, userMetaSaved);

        // logout if downgrading current user from admin to user
        if (usersComboRole.getValue().toString().contains("User") && Main.userLoggedIn.getUser_id() == Integer.parseInt(usersInputUserId.getText().strip()) ){
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
            User user = (User) usersTable.getSelectionModel().getSelectedItem();
            user.delete();
            usersBtnClear.fire(); // clear form
            showUsers(); // refresh users list
        }

    }

    private boolean confirmAction(String action){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to " + action);
        User user = (User) usersTable.getSelectionModel().getSelectedItem();
        alert.setContentText(
                        "User ID: " + user.getUser_id() + "\n" +
                        "Username: " + user.getUsername() + "\n" +
                        "Firstname: " + user.getFirstname() + "\n" +
                        "Lastname: " + user.getLastname() + "\n"
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

    @FXML
    public void BtnBackAction(ActionEvent evt) {
        Main.loadScene(evt, "configuration", false);
    }


    @FXML
    private void tableViewClickedAction(MouseEvent event){
        System.out.println("table clicked!");
        User user = (User) usersTable.getSelectionModel().getSelectedItem();

        // Populate the form inout TextFields
        usersInputUserId.setText(String.valueOf(user.getUser_id()).strip());
        usersInputUserId.setEditable(false); // Disable user_id field
        usersInputFirstname.setText(user.getFirstname().strip());
        usersInputLastname.setText(user.getLastname().strip());
        usersInputUsername.setText(user.getUsername().strip());
        usersInputPassword.setText(user.getPassword().strip());
        usersComboRole.setValue(user.getRoles().get(0));
    }

}
