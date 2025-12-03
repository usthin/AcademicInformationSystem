package models;

public class Teacher extends User {
    public Teacher(int id, String firstName, String lastName, String login, String password) {
        super(id, firstName, lastName, login, password, "TEACHER");
    }
}
