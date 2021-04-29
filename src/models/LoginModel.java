package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginModel {

    private final DaoModel dao = new DaoModel();
    private final String usersTable =dao.getTableName("users");

    public boolean login(String username, String
            password){

        String sql = "Select * from " + usersTable
                + " where username='" + username
                + "' and password='" + password + "'";

        try{
            // execute sql with user input username and password
            ResultSet rs = new DbConnect().connect().createStatement().executeQuery(sql);
            // check if the entered credential matches any records in the users table
            return rs.next();

        }
        catch (SQLException se){se.printStackTrace();}

        return false;
    }



}
