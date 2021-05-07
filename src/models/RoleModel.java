package models;

import java.util.Vector;

public class RoleModel {
    private String role;
    private Vector<String> capabilities;

    public RoleModel(String role, Vector<String> capabilities) {
        this.role = role;
        this.capabilities = capabilities;
    }

    public RoleModel() {

    }

    public String getRole() {
        return role;
    }

    public RoleModel setRole(String role) {
        this.role = role;
        return this;
    }

    public Vector<String> getCapabilities() {
        return capabilities;
    }

    public RoleModel setCapabilities(Vector<String> capabilities) {
        this.capabilities = capabilities;
        return this;
    }
}
