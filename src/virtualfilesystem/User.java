/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualfilesystem;

import java.util.ArrayList;

/**
 *
 * @author kerols
 */
public class User {

    String name;
    String password;
    ArrayList<String> path = new ArrayList<>();
    ArrayList<Integer> Capabilities = new ArrayList<>();

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getPath() {
        return path;
    }

    public void setPath(ArrayList<String> path) {
        this.path = path;
    }

    public ArrayList<Integer> getCapabilities() {
        return Capabilities;
    }

    public void setCapabilities(ArrayList<Integer> Capabilities) {
        this.Capabilities = Capabilities;
    }

}
