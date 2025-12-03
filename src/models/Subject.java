package models;

public class Subject {
    public int id;
    public String name;
    public Integer teacherId;

    public Subject(int id, String name, Integer teacherId) {
        this.id = id;
        this.name = name;
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return id + ": " + name + (teacherId == null ? "" : " (t=" + teacherId + ")");
    }
}
