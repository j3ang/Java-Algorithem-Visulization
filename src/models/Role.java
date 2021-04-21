package models;

import java.util.Vector;

public class Role {
    private String role;
    private Vector<String> capabilities;

    public Role(String role, Vector<String> capabilities) {
        this.role = role;
        this.capabilities = capabilities;
    }

    public Role() {

    }

    public String getRole() {
        return role;
    }

    public Role setRole(String role) {
        this.role = role;
        return this;
    }

    public Vector<String> getCapabilities() {
        return capabilities;
    }

    public Role setCapabilities(Vector<String> capabilities) {
        this.capabilities = capabilities;
        return this;
    }
}
