package models;


import com.google.gson.Gson;
import controllers.SignupController;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;

public class DaoModel<T> {

    // Declare DB objects
    DbConnect conn = null;
    Dotenv dotenv = Dotenv.load();
    String tablePrefix = dotenv.get("DB_PREFIX");

    public DbConnect getConnection(){
        return conn;
    }

    String[] tableNames ={
            tablePrefix+"algorithms",
            tablePrefix+"options",
            tablePrefix+"capabilities",
            tablePrefix+"roles",
            tablePrefix+"users",
            tablePrefix+"activities",
            tablePrefix+"usermeta",
    };


    // Constructor
    public DbConnect DaoModel() {
        try {
            this.conn = new DbConnect();
            System.out.printf("conn:" + this.conn);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return conn;

    }

    public void executeStatement(String tableName, String sql){
       try{
           PreparedStatement pStmt = new DbConnect().connect().prepareStatement(sql);
           System.out.println(tableName);
           System.out.println("Executed Statement: " + pStmt);
           pStmt.executeUpdate();
           pStmt.close();
           System.out.println("Connection closed.");
       } catch ( SQLException se ){
           se.printStackTrace();
       }
    }

    // CREATE TABLE METHOD
    public void createTable() {
        if (  !rowExists(getTableName("users"), "user_id", dotenv.get("ADMIN_ID")) ){
            // Create tables
            for (String tableName : tableNames) {
                // Option table where user roles are stored
                if (tableName.contains("options")){
                    // Prepare statement
                    String algorithmsSQL = "CREATE TABLE IF NOT EXISTS " + tableName  + " " +
                            "(option_id INTEGER not NULL AUTO_INCREMENT, " +
                            " option_key VARCHAR(50), " +
                            " option_value TEXT, " +
                            " PRIMARY KEY ( option_id ))";

                    // Execute prepared statement
                    executeStatement(tableName, algorithmsSQL);
                }

                // where capabilities are stored and can be added to user roles
                if (tableName.contains("capabilities")){
                    // Prepare statement
                    String capabilitiesSQL = "CREATE TABLE IF NOT EXISTS " + tableName  + " " +
                            "(cap_id INTEGER not NULL AUTO_INCREMENT, " +
                            " cap_name VARCHAR(100), " +
                            " cap_desc VARCHAR(100), " +
                            " cap_dt_created TIMESTAMP NOT NULL, " +
                            " PRIMARY KEY ( cap_id ))";

                    // Execute prepared statement
                    executeStatement(tableName, capabilitiesSQL);
                }


                if (tableName.contains("algorithms")){
                    // Prepare statement
                    String algorithmsSQL = "CREATE TABLE IF NOT EXISTS " + tableName  + " " +
                            "(algo_id INTEGER not NULL AUTO_INCREMENT, " +
                            " algo_name VARCHAR(50), " +
                            " algo_desc VARCHAR(100), " +
                            " algo_dt_created TIMESTAMP NOT NULL, " +
                            " PRIMARY KEY ( algo_id ))";

                    // Execute prepared statement
                    executeStatement(tableName, algorithmsSQL);
                }



                if (tableName.contains("users")){
                    // Prepare statement to execute
                    String usersSQL = "CREATE TABLE IF NOT EXISTS " + tableName  + " " +
                            "(user_id INTEGER not NULL AUTO_INCREMENT, " +
                            " firstname VARCHAR(30), " +
                            " lastname VARCHAR(30), " +
                            " username VARCHAR(30), " +
                            " password VARCHAR(64), " +
                            " dt_created TIMESTAMP NOT NULL, " +
                            " PRIMARY KEY ( user_id ))";

                    // Execute create query
                    executeStatement(tableName, usersSQL);
                }

                if (tableName.contains("activities")){
                    // Prepare statement to execute
                    String activitiesSQL = "CREATE TABLE IF NOT EXISTS " + tableName  + " " +
                            "(act_id INTEGER not NULL AUTO_INCREMENT, " +
                            " user_id INTEGER not NULL, " +
                            " act_sort_task TEXT NOT NULL, " +
                            " act_sort_size INTEGER NOT NULL, " +
                            " act_sort_speed TEXT NOT NULL, " +
                            " act_sort_duration TEXT NOT NULL, " +
                            " act_dt_created TIMESTAMP NOT NULL, " +
                            " PRIMARY KEY ( act_id ), " +
                            " FOREIGN KEY (user_id) REFERENCES " + tablePrefix + "users(user_id))";

                    // Execute create query
                    executeStatement(tableName, activitiesSQL);
                }

                if (tableName.contains("usermeta")){
                    // Prepare statement to execute
                    String activitiesSQL = "CREATE TABLE IF NOT EXISTS " + tableName  + " " +
                            "(meta_id INTEGER not NULL AUTO_INCREMENT, " +
                            " user_id INTEGER not NULL, " +
                            " meta_name VARCHAR(50), " +
                            " meta_desc VARCHAR(100), " +
                            " PRIMARY KEY ( meta_id ), " +
                            " FOREIGN KEY (user_id) REFERENCES " + tablePrefix + "users(user_id))";

                    // Execute create query
                    executeStatement(tableName, activitiesSQL);
                }

            }
        }

    }

    public void setupRoot(){
        if ( !rowExists(getTableName("users"), "user_id", dotenv.get("ADMIN_ID")) ){
            if (!rowExists(getTableName("users"), "user_id", dotenv.get("ADMIN_ID")) ){
                // Create root user if not exist
                UserModel admin = new UserModel(Integer.parseInt(dotenv.get("ADMIN_ID")),
                        dotenv.get("ADMIN_FIRSTNAME"),
                        dotenv.get("ADMIN_LASTNAME"),
                        dotenv.get("ADMIN_USERNAME"),
                        SignupController.hashPassword(dotenv.get("ADMIN_PASS")));
                admin.save(this, true, true);
            }

            // Create default capabilities
            if (!rowExists(getTableName("capabilities"), "cap_name", "can_create_user")){
                new CapabilityModel("can_create_user", "can create new user").add();
                new CapabilityModel("can_read_user", "can retrieve and view list of users").add();
                new CapabilityModel("can_update_user", "can modify and save user data").add();
                new CapabilityModel("can_delete_user", "can delete user").add();

                new CapabilityModel("can_create_history", "can create history record").add();
                new CapabilityModel("can_read_history", "can retrieve and view list of history records").add();
                new CapabilityModel("can_update_history", "can modify and update history records").add();
                new CapabilityModel("can_delete_history", "can delete history records").add();
            }

            // Add Admin role in options table
            if (! rowExists(getTableName("options"), "option_key", "role_capabilities") ){
                // Get all capabilities inserted above
                Vector<Vector<Object>> capData = readData(new CapabilityModel().getAllCapabilities(), getTableName("capabilities"));

                // Assign capabilities to admin role
                Vector adminCaps = new Vector();
                for( Object cap : capData ){
                    adminCaps.add(cap);  // all capabilities available
                }

                // Assign capabilities to user role
                Vector<String> userCaps = new Vector<>();
                userCaps.add("can_create_history");
                userCaps.add("can_read_history");
                userCaps.add("can_delete_history");

                // Create new admin and user roles
                RoleModel admin = new RoleModel("Administrator", adminCaps);
                RoleModel user = new RoleModel("User", userCaps);

                // The role_capabilities option value contains the list of role cap pairs
                HashMap<String, Vector> role_cap = new HashMap<>();
                role_cap.put( admin.getRole(), admin.getCapabilities() );
                role_cap.put( user.getRole(), user.getCapabilities() );

                // Save Option
                OptionModel optionModel = new OptionModel("role_capabilities", role_cap );
                optionModel.save(false);


                System.out.println("saved option " + optionModel.getOption_key() );
                System.out.println("option value: " + optionModel.getOption_value() );

            }

            // Assign role to User in Usermeta table // Create root user
            if (! rowExists(getTableName("usermeta"), "user_id", dotenv.get("ADMIN_ID")) ){
                ArrayList<String> roles  = new ArrayList<>();
                roles.add("Administrator");

                Gson gson = new Gson();
                String rolesJson = gson.toJson(roles);

                UsermetaModel adminMeta = new UsermetaModel( Integer.parseInt(dotenv.get("ADMIN_ID")), "user_roles",  rolesJson );
                adminMeta.save(false);
            }
        }
    }

    public Boolean rowExists(String tableName, String colName, String colVal){
        ResultSet rs = null;
        String sql = "select * from " + tableName +" where "+colName+"='" + colVal + "'";

        try{
            PreparedStatement sqlstmt = new DbConnect().connect().prepareStatement(sql);
            rs = sqlstmt.executeQuery();
            if ( readDataSize(rs, tableName) > 0){
                return true;
            }

            sqlstmt.close(); // close statement
			rs.close();
        } catch (SQLException se){
            se.printStackTrace();
        }

        return false;
    }

    public int readDataSize(ResultSet rs, String tableName){
        // instantiate vector objects to hold column/row data for JTable
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        Vector<String> column = new Vector<String>();
        int size  = 0;  // table rcord counter

        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columns = metaData.getColumnCount();

            // get column names from table!
            String cols = "";
            for (int i = 1; i <= columns; i++) {
                cols = metaData.getColumnName(i);
                column.add(cols);
            }
            // get row data from table!
            while (rs.next()) {
                Vector<Object> row = new Vector<Object>(columns);
                for (int i = 1; i <= columns; i++)
                    row.addElement(rs.getObject(i));
                data.addElement(row);
                size++;
            }
        } catch (SQLException e) { e.printStackTrace(); } finally {
            try {
                rs.close(); //close ResultSet instance
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return size;
    }

    public Vector<Vector<Object>> readData(ResultSet rs, String tableName){
        // instantiate vector objects to hold column/row data for JTable
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        Vector<String> column = new Vector<String>();

        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columns = metaData.getColumnCount();

            // get column names from table!
            String cols = "";
            for (int i = 1; i <= columns; i++) {
                cols = metaData.getColumnName(i);
                column.add(cols);
            }

            // get row data from table!
            while (rs.next()) {
                Vector<Object> row = new Vector<Object>(columns);
                for (int i = 1; i <= columns; i++)
                    row.addElement(rs.getObject(i));
                data.addElement(row);

            }


        } catch (SQLException e) { e.printStackTrace(); } finally {
            try {
                rs.close(); //close ResultSet instance
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    // prepare statement with arraylist
    public  String prepareInsertStmt(String tableName,
                                     Vector<String> tableColumns,
                                     ArrayList<String> objVal,
                                     Boolean includeId ){

        if (!includeId){
            tableColumns.remove(0);
        }

        String columnsToInsert = " (" + String.join(", ", tableColumns) + ") ";
        String preparedSQL = "Insert into " + tableName + columnsToInsert + "VALUES (";
        StringBuilder sbPreparedSQL = new StringBuilder();
        sbPreparedSQL.append(preparedSQL);
        System.out.println("prepareInsertStmt columns: " + columnsToInsert);

        // build values part
        for (int i=0; i <tableColumns.size(); i++){
            if (tableColumns.get(i).contains("dt") ){
                sbPreparedSQL.append("NOW()");
                break;
            } else  {
                if (i == tableColumns.size()-1) {
                    // end of statement don't need comma delimiter
                    sbPreparedSQL.append("'" + objVal.get(i) + "'");
                } else {
                    sbPreparedSQL.append("'" + objVal.get(i) + "',");
                }
            }
        }

        sbPreparedSQL.append(")");

        return sbPreparedSQL.toString();
    }

    // prepare statement for select operation
    public String prepareUpdateStmt(String tableName,
                                    Vector<String> tableColumns,
                                    ArrayList<String> objVal,
                                    String whereClause){


        if ( tableName.equals(getTableName("users"))){
            removeColumn(tableColumns, "user_id");  // remove the id column for update
            removeColumn(tableColumns, "created"); // remove the dt_created column for update, keep the updated date in user meta
        }

        if (tableName.equals(getTableName("usermeta"))){
            removeColumn(tableColumns, "meta_id");  // remove the id column for update
            removeColumn(tableColumns, "user_id");  // remove the id column for update
        }

        System.out.println("    received array obj: " + objVal);
        System.out.println("updating table columns: " + tableColumns.toString());


        // Start string builder
        String updateSQL = "Update " + tableName + " SET ";
        StringBuilder sbPreparedUpdateSQL = new StringBuilder();
        sbPreparedUpdateSQL.append(updateSQL);

        // loop through columns
        for(int i=0; i < tableColumns.size(); i++){
            if (i == tableColumns.size()-1) {
                // end of statement don't need comma delimiter
                sbPreparedUpdateSQL.append(tableColumns.get(i) + "='" + objVal.get(i) + "' ");
            } else {
                sbPreparedUpdateSQL.append(tableColumns.get(i) + "='" + objVal.get(i) + "', ");
            }
        }

        String preparedUpdateSQL = sbPreparedUpdateSQL.append(whereClause).toString();
        System.out.println("prepared update statement: " + preparedUpdateSQL);
        return preparedUpdateSQL;
    }

    public Vector<String> removeColumn(Vector<String> tableColumns, String regex){

        Pattern p = Pattern.compile(regex);

        for ( int i=0; i<tableColumns.size(); i++) {

            if (p.matcher(tableColumns.get(i)).find()){
                tableColumns.remove(i);
            }
        }

        System.out.println("DaoModel.removeColumn():  " +  tableColumns );
        return tableColumns;
    }

    // prepare statement for select operation
    public String prepareSelectStmt(String tableName, String whereClause){
        String preparedSelectSQL = "Select * from " + tableName + " where " + whereClause;
        return preparedSelectSQL;
    }

    public String getTableName(String tablename){
        String TableName = tablePrefix + tablename;
        if (Arrays.asList(tableNames).contains(TableName)){
            return TableName;
        }
        return "";
    }

    public Vector<String> getTableCols(String tableName){
        Vector<String> column = new Vector<String>();
        String sql = "select * from "+ tableName+" limit 1";

        // get column names from table!
        try{
            PreparedStatement sqlstmt = new DbConnect().connect().prepareStatement(sql);
            ResultSet rs = sqlstmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columns = metaData.getColumnCount();

            String cols = "";
            for (int i = 1; i <= columns; i++) {
                cols = metaData.getColumnName(i);
                column.add(cols);
            }
        } catch (SQLException se){
            se.printStackTrace();
        }

        return column;
    }
}


