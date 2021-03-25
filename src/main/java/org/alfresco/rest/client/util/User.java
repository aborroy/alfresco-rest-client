package org.alfresco.rest.client.util;

public class User {

    String user;
    String pass;

    public User user(String user) {
        this.user = user;
        return this;
    }

    public User pass(String pass) {
        this.pass = pass;
        return this;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
