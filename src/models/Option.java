package models;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class Option {

    private int option_id;
    private String option_key;
    private HashMap<String, Vector> option_value;
    private final String optionsTable = new DaoModel<>().tablePrefix + "options";


    public Option() {
    }

    public Vector<Role> decodeOptionValue(String json){

        Vector<Role> roles = new Vector<>();

        Type listType = new TypeToken<HashMap<String, Vector>>(){}.getType();
        HashMap<String, Vector> role_caps = new Gson().fromJson(json, listType);
        for (Map.Entry<String, Vector> entry : role_caps.entrySet()) {
            String key = entry.getKey();
            Vector value = entry.getValue();
            roles.add(new Role(key, value));
            System.out.println("Option.decodeOptionValue(): " + key + ": " + value );
        }

        return roles;
    }

    public void save(Boolean includeId) {

        DaoModel dao = new DaoModel<>();
        Gson gson = new Gson();
        String option_value_json = gson.toJson(option_value);


        ArrayList<String> optionArr = new ArrayList<>();
        optionArr.add(option_key);
        optionArr.add(option_value_json);

        String sql = dao.prepareInsertStmt(optionsTable,
                dao.getTableCols(optionsTable),optionArr, includeId);

        System.out.println("Option.save(): option_value_json = " + option_value_json);
        System.out.println("Option.save(): " + sql);
        dao.executeStatement(optionsTable, sql);

    }

    public Vector<Role> getRoleCapOption(String optionKey){
        DaoModel dao = new DaoModel();
        ResultSet rs = null;
        Vector<Role> decoded_role_caps = new Vector<>();

        String sql = dao.prepareSelectStmt(dao.getTableName("options"), "option_key='" + optionKey + "'");

        try{
            System.out.println();
            System.out.println("Option.getRoleCapOption(): executing query " + sql);
            rs = new DbConnect().connect().createStatement().executeQuery(sql);
            while(rs.next()){
//                System.out.println("option: " +
//                        rs.getString(1) + " " + // id
//                        rs.getString(2) + " " + // option_key
//                        rs.getString(3) );      // option_value
                // decodeOptionValue returns vectors of roles
                decoded_role_caps =  decodeOptionValue(rs.getString(3));
            }
            rs.close();
            System.out.println("Option.getRoleCapOption(): rs connection closed \n");
        } catch (SQLException se){
            se.printStackTrace();
        }

        return decoded_role_caps;
    }


    public Option(String option_key, HashMap<String, Vector> option_value) {
        this.option_key = option_key;
        this.option_value = option_value;
    }

    public Option(int option_id, String option_key, HashMap<String, Vector> option_value) {
        this.option_id = option_id;
        this.option_key = option_key;
        this.option_value = option_value;
    }

    public int getOption_id() {
        return option_id;
    }

    public Option setOption_id(int option_id) {
        this.option_id = option_id;
        return this;
    }

    public String getOption_key() {
        return option_key;
    }

    public Option setOption_key(String option_key) {
        this.option_key = option_key;
        return this;
    }

    public HashMap<String, Vector> getOption_value() {
        return option_value;
    }

    public Option setOption_value(HashMap<String, Vector> option_value) {
        this.option_value = option_value;
        return this;
    }


}
