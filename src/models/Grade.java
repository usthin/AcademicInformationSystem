package models;

public class Grade {
    public int id;
    public int studentId;
    public int subjectId;
    public String grade;

    public Grade(int id, int studentId, int subjectId, String grade) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.grade = grade;
    }
}
