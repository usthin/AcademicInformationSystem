package models;

public abstract class User {
    protected int id;
    protected String firstName;
    protected String lastName;
    protected String login;
    protected String password;
    protected String role;

    public User(int id, String firstName, String lastName, String login, String password, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    @Override
    public String toString() {
        return id + ": " + firstName + " " + lastName;
    }
}
