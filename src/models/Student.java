package models;

public class Student extends User {
    public Student(int id, String firstName, String lastName, String login, String password) {
        super(id, firstName, lastName, login, password, "STUDENT");
    }
}
