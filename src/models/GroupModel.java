package models;

public class GroupModel {
    public int id;
    public String name;

    public GroupModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() { return id + ": " + name; }
}
