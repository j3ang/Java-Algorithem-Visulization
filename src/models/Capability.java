package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Capability {
    private int cap_id;
    private String cap_name, cap_desc, cap_dt_created;
    DaoModel dao = new DaoModel();
    String capabilitiesTable = dao.getTableName("capabilities");

    public Capability() {
    }

    public Capability( String cap_name, String cap_desc) {
        this.cap_name = cap_name;
        this.cap_desc = cap_desc;
    }


    public Capability(int cap_id, String cap_name, String cap_desc, String cap_dt_created) {
        this.cap_id = cap_id;
        this.cap_name = cap_name;
        this.cap_desc = cap_desc;
        this.cap_dt_created = cap_dt_created;
    }


    public void add(){
        Vector capColsToInsert =  dao.getTableCols(capabilitiesTable) ;

        // Prepare statement
        ArrayList<String> capability = new ArrayList<>();
        capability.add(cap_name);
        capability.add(cap_desc);

        String sql = dao.prepareInsertStmt(capabilitiesTable, capColsToInsert, capability, false);

        System.out.println(sql);

        // Execute prepared statement
        dao.executeStatement(capabilitiesTable, sql);
    }

    public ResultSet getAllCapabilities(){
        ResultSet rs = null;
        String sql = "select * from " + capabilitiesTable;

        try{
            PreparedStatement sqlstmt = dao.conn.connect().prepareStatement(sql);
            rs = sqlstmt.executeQuery(sql);

        } catch (SQLException se){
            se.printStackTrace();
        }

        return rs;
    }

    public int getCap_id() {
        return cap_id;
    }

    public Capability setCap_id(int cap_id) {
        this.cap_id = cap_id;
        return this;
    }

    public String getCap_name() {
        return cap_name;
    }

    public Capability setCap_name(String cap_name) {
        this.cap_name = cap_name;
        return this;
    }

    public String getCap_desc() {
        return cap_desc;
    }

    public Capability setCap_desc(String cap_desc) {
        this.cap_desc = cap_desc;
        return this;
    }

    public String getCap_dt_created() {
        return cap_dt_created;
    }

    public Capability setCap_dt_created(String cap_dt_created) {
        this.cap_dt_created = cap_dt_created;
        return this;
    }
}
