package models;

public class Administrator extends User {
    public Administrator(int id, String firstName, String lastName, String login, String password) {
        super(id, firstName, lastName, login, password, "ADMIN");
    }
}
