package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class Usermeta {
    private int meta_id, user_id;
    private String mata_name, meta_desc;

    public Usermeta() {
    }


    public Usermeta(int user_id, String mata_name, String meta_desc) {
        this.user_id = user_id;
        this.mata_name = mata_name;
        this.meta_desc = meta_desc;
    }

    public Usermeta(int meta_id, int user_id, String mata_name, String meta_desc) {
        this.meta_id = meta_id;
        this.user_id = user_id;
        this.mata_name = mata_name;
        this.meta_desc = meta_desc;
    }

    public void save(boolean includeId) {
        DaoModel dao = new DaoModel();
        String usermetaTable =  dao.tablePrefix + "usermeta";

        Vector<String> usermetaCols = dao.getTableCols(usermetaTable);
        ArrayList<String> metaArr = new ArrayList<>();

        if (includeId){
            metaArr.add(String.valueOf(meta_id));
        }

        metaArr.add(String.valueOf(user_id));
        metaArr.add(mata_name);
        metaArr.add(meta_desc);

        String saveUserSql =  dao.prepareInsertStmt(usermetaTable,usermetaCols, metaArr, includeId);
        dao.executeStatement(usermetaTable, saveUserSql);
    }


    public int getMeta_id() {
        return meta_id;
    }

    public Usermeta setMeta_id(int meta_id) {
        this.meta_id = meta_id;
        return this;
    }

    public int getUser_id() {
        return user_id;
    }

    public Usermeta setUser_id(int user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getMata_name() {
        return mata_name;
    }

    public Usermeta setMata_name(String mata_name) {
        this.mata_name = mata_name;
        return this;
    }

    public String getMeta_desc() {
        return meta_desc;
    }

    public Usermeta setMeta_desc(String meta_desc) {
        this.meta_desc = meta_desc;
        return this;
    }


}
