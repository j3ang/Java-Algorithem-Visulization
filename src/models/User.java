package models;

import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class User {
    private int user_id;
    private String firstname, lastname, username, password, dt_created;
    private ArrayList<String>roles;
    DaoModel dao = new DaoModel();
    String usersTable = dao.getTableName("users");

    public User(int user_id, String firstname, String lastname, String username, String password, String dt_created) {
        this.user_id = user_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.dt_created = dt_created;
    }

    public User(int user_id, String firstname, String lastname, String username, String password) {
        this.user_id = user_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
    }

    public User(String firstname, String lastname, String username, String password, ArrayList<String>roles) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public User(String firstname, String lastname, String username, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    public User(int user_id, ArrayList<String> roles) {
        this.user_id = user_id;
        this.roles = roles;
    }

    public void delete(){
        String usersTable =  dao.getTableName("users");
        String uMetatable = dao.getTableName("usermeta");

        // delete from usermeta table as it's foreign key constraint
        String deleteUserMetaQuery = "Delete from " + uMetatable + " where user_id=" + user_id;
        dao.executeStatement(usersTable, deleteUserMetaQuery);

        // delete from users table
        String deleteUserQuery = "Delete from " + usersTable + " where user_id=" + user_id;
        dao.executeStatement(usersTable, deleteUserQuery);
    }

    public void save(boolean includeId, boolean newUser){

        String usersTable =  dao.tablePrefix + "users";
        Vector<String> userCols = dao.getTableCols(usersTable);
        ArrayList<String> userArr = new ArrayList<>();

        // include user_id column if it's saving a new user
        if (includeId && newUser){
            userArr.add(String.valueOf(user_id));
        }

        userArr.add(firstname);
        userArr.add(lastname);
        userArr.add(username);
        userArr.add(password);
        userArr.add(new Gson().toJson(roles));

        // new user or update user
        String sql = newUser ?
                dao.prepareInsertStmt(usersTable,userCols, userArr, includeId)
                :  dao.prepareUpdateStmt(usersTable,userCols, userArr, "where user_id=" + user_id);

        dao.executeStatement(usersTable, sql);


    }

    public static void getUserRole(DaoModel dao, User user){

        try{
            ArrayList<String> thisUserRoles = new ArrayList<>();
            String sql = "SELECT meta_desc from " + dao.getTableName("usermeta") +
                    " where user_id="+ user.getUser_id() +" and meta_name='user_roles'";
            ResultSet rs =new DbConnect().connect().createStatement().executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            if (rs.next()) { // add to the Observable array list
                for (int j = 1; j <= columnsNumber; j++) {
                    if (j > 1) System.out.print(",  ");
                    String columnValue = rs.getString(j).replaceAll("\\p{Punct}", "");
                    thisUserRoles.add(columnValue);
                    user.setRoles(thisUserRoles);
                }
            }

            rs.close();
        } catch (SQLException se){
            se.printStackTrace();
        }

    }

    public static void setUserRole(DaoModel dao, User user){
        System.out.println("set user role: " + user.getRoles());

        // set user roles in usermeta
        String query = "Select * from " + dao.getTableName("usermeta") +
                " where meta_name='user_roles' and " +
                " user_id=" + user.getUser_id();

        try{
            ResultSet usermetaRs  =new DbConnect().connect().createStatement().executeQuery(query);
            String userMetaTable = dao.getTableName("usermeta");

            ArrayList uMetaArr = new ArrayList();
            uMetaArr.add("user_roles");
            uMetaArr.add( new Gson().toJson(user.getRoles()) );

            // update usermeta user_roles
            if ( dao.readDataSize( usermetaRs , userMetaTable) > 0 ){
                String updateUsermeta = dao.prepareUpdateStmt(
                        userMetaTable, dao.getTableCols(userMetaTable),
                        uMetaArr, "where user_id=" + user.getUser_id() );

                dao.executeStatement(dao.getTableName("usermeta"), updateUsermeta);
                System.out.println("executed updateUsermeta: " + updateUsermeta);

            } else { // create new usermeta user_roles
                Usermeta userMeta = new Usermeta(
                        user.getUser_id(), "user_roles", new Gson().toJson(user.getRoles()) );
                userMeta.save(false);
            }

        } catch (SQLException se){
            se.printStackTrace();
        }

    }


    public static Vector getAllUsers(){
        DaoModel dao = new DaoModel();
        String  usersTable = dao.getTableName("users");
        String sql = dao.prepareSelectStmt(usersTable, "1=1");
        ResultSet rs = null;
        Vector userRs = new Vector();
        try{
             rs =new DbConnect().connect().createStatement().executeQuery(sql);
             userRs = dao.readData(rs,usersTable);
        } catch (SQLException se){
            se.printStackTrace();
        }

        return userRs;
    }



    public static User getUserByUsername(String username){
        User user = new User();
        DaoModel dao = new DaoModel();
        String  usersTable = dao.getTableName("users");

        String whereClause = "username='" + username + "'";

        String sql = dao.prepareSelectStmt(usersTable, whereClause);
        try{
            System.out.println("User.getUserByUsername: creating new DbConnect()");
            ResultSet rs = new DbConnect().connect().createStatement().executeQuery(sql);
            Vector userRs = dao.readData(rs,usersTable);
            String[] parsedUser = userRs.get(0).toString().replaceAll("\\p{P}","").split(" ");

            // pull user result data to the user object
            user = vectorToUser(parsedUser);
            rs.close();
        }catch (SQLException se){
            se.printStackTrace();
        }

        // add role from usermeta
        getUserRole(dao, user);

        return user;
    }


    public User getUserById(int user_id){
        User user = new User();
        String whereClause = "user_id=" + user_id;

        String sql = dao.prepareSelectStmt(usersTable, whereClause);
        try{
            ResultSet rs =new DbConnect().connect().createStatement().executeQuery(sql);
            Vector userRs = dao.readData(rs,usersTable);
            String[] parsedUser = userRs.get(0).toString().replaceAll("\\p{P}","").split(" ");

            // pull user result data to the user object
            user = vectorToUser(parsedUser);


        }catch (SQLException se){
            se.printStackTrace();
        }

        return user;
    }

    public static User vectorToUser(String[] parsedUser){
        User user = new User(
                Integer.parseInt(parsedUser[0]),
                parsedUser[1],
                parsedUser[2],
                parsedUser[3],
                parsedUser[4]
        );
        return user;
    }

    public int getUser_id() {
        return user_id;
    }

    public User setUser_id(int user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public User setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public User setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDt_created() {
        return dt_created;
    }

    public User setDt_created(String dt_created) {
        this.dt_created = dt_created;
        return this;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public User setRoles(ArrayList<String> roles) {
        this.roles = roles;
        return this;
    }


}
