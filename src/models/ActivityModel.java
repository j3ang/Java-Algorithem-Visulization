package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class ActivityModel {

    private int act_id;
    private String user_id;
    private int act_sort_size;
    private String act_sort_task;
    private String act_sort_duration;
    private String act_sort_speed;
    private String act_dt_created;
    private final transient DaoModel dao;

    public ActivityModel(){
        this.dao = new DaoModel<>();
    }

    public int getAct_id() {
        return act_id;
    }

    public void setAct_id(int act_id) {
        this.act_id = act_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public ActivityModel setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public int getAct_sort_size() {
        return act_sort_size;
    }

    public ActivityModel setAct_sort_size(int act_sort_size) {
        this.act_sort_size = act_sort_size;
        return this;
    }

    public String getAct_sort_speed() {
        return act_sort_speed;
    }

    public ActivityModel setAct_sort_speed(String act_sort_speed) {
        this.act_sort_speed = act_sort_speed;
        return this;
    }

    public String getAct_sort_task() {
        return act_sort_task;
    }

    public ActivityModel setAct_sort_task(String act_sort_task) {
        this.act_sort_task = act_sort_task;
        return this;
    }

    public String getAct_sort_duration() {
        return act_sort_duration;
    }

    public ActivityModel setAct_sort_duration(String act_sort_duration) {
        this.act_sort_duration = act_sort_duration;
        return this;
    }

    public void save(){
        String activitiesTable = dao.getTableName("activities");
        ArrayList<String> logValue = new ArrayList<>();
        logValue.add(user_id);
        logValue.add(act_sort_task);
        logValue.add(String.valueOf(act_sort_size));
        logValue.add(act_sort_speed);
        logValue.add(act_sort_duration);
        logValue.add("NOW()");

        String sql = dao.prepareInsertStmt(
                activitiesTable,
                dao.getTableCols(activitiesTable),
                logValue,
                false);

        dao.executeStatement(activitiesTable, sql);

    }

    public String getAct_dt_created() { return act_dt_created; }

    public void setAct_dt_created(String act_dt_created) {
        this.act_dt_created = act_dt_created;
    }

    public Vector getActivitiesByUserID(int userId) {
        String  activitiesTable = dao.getTableName("activities");
        String sql = dao.prepareSelectStmt(activitiesTable, "user_id=" + userId);
        ResultSet rs = null;
        Vector activitiesRs = new Vector();
        try{
            rs =new DbConnect().connect().createStatement().executeQuery(sql);
            activitiesRs = dao.readData(rs,activitiesTable);
        } catch (SQLException se){
            se.printStackTrace();
        }

        return activitiesRs;
    }
}
