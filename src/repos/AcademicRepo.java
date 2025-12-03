package repos;

import db.DB;
import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AcademicRepo {

    // Teachers
    public static Teacher createTeacher(String firstName, String lastName) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ins = c.prepareStatement("INSERT INTO Users(firstName,lastName,login,password,role) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            String login = firstName;
            String pw = lastName;
            ins.setString(1, firstName);
            ins.setString(2, lastName);
            ins.setString(3, login);
            ins.setString(4, pw);
            ins.setString(5, "TEACHER");
            ins.executeUpdate();
            ResultSet gk = ins.getGeneratedKeys();
            if (gk.next()) {
                int id = gk.getInt(1);
                try (PreparedStatement ins2 = c.prepareStatement("INSERT INTO Teachers(id) VALUES(?)")) {
                    ins2.setInt(1, id);
                    ins2.executeUpdate();
                }
                return new Teacher(id, firstName, lastName, login, pw);
            }
        }
        return null;
    }

    public static List<Teacher> listTeachers() throws SQLException {
        List<Teacher> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT u.* FROM Users u WHERE u.role='TEACHER'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Teacher(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("login"), rs.getString("password")));
            }
        }
        return list;
    }

    // Students
    public static Student createStudent(String firstName, String lastName) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ins = c.prepareStatement("INSERT INTO Users(firstName,lastName,login,password,role) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            String login = firstName;
            String pw = lastName;
            ins.setString(1, firstName);
            ins.setString(2, lastName);
            ins.setString(3, login);
            ins.setString(4, pw);
            ins.setString(5, "STUDENT");
            ins.executeUpdate();
            ResultSet gk = ins.getGeneratedKeys();
            if (gk.next()) {
                int id = gk.getInt(1);
                try (PreparedStatement ins2 = c.prepareStatement("INSERT INTO Students(id) VALUES(?)")) {
                    ins2.setInt(1, id);
                    ins2.executeUpdate();
                }
                return new Student(id, firstName, lastName, login, pw);
            }
        }
        return null;
    }

    public static List<Student> listStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT u.* FROM Users u WHERE u.role='STUDENT'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Student(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("login"), rs.getString("password")));
            }
        }
        return list;
    }

    // Generic delete user (used for deleting teacher/student)
    public static void deleteUserById(int userId) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Users WHERE id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // Subjects
    public static Subject createSubject(String name) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ins = c.prepareStatement("INSERT INTO Subjects(name, teacherId) VALUES(?,NULL)", Statement.RETURN_GENERATED_KEYS)) {
            ins.setString(1, name);
            ins.executeUpdate();
            ResultSet gk = ins.getGeneratedKeys();
            if (gk.next()) return new Subject(gk.getInt(1), name, null);
        }
        return null;
    }

    public static List<Subject> listSubjects() throws SQLException {
        List<Subject> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM Subjects")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Subject(rs.getInt("id"), rs.getString("name"),
                    (rs.getObject("teacherId") != null) ? rs.getInt("teacherId") : null));
        }
        return list;
    }

    public static void assignTeacherToSubject(int subjectId, Integer teacherId) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE Subjects SET teacherId = ? WHERE id = ?")) {
            if (teacherId == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, teacherId);
            ps.setInt(2, subjectId);
            ps.executeUpdate();
        }
    }

    public static void deleteSubject(int subjectId) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Subjects WHERE id = ?")) {
            ps.setInt(1, subjectId);
            ps.executeUpdate();
        }
    }

    // Groups
    public static GroupModel createGroup(String name) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ins = c.prepareStatement("INSERT INTO GroupsTable(name) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
            ins.setString(1, name);
            ins.executeUpdate();
            ResultSet gk = ins.getGeneratedKeys();
            if (gk.next()) return new GroupModel(gk.getInt(1), name);
        }
        return null;
    }

    public static List<GroupModel> listGroups() throws SQLException {
        List<GroupModel> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM GroupsTable")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new GroupModel(rs.getInt("id"), rs.getString("name")));
        }
        return list;
    }

    public static void assignStudentToGroup(int studentId, int groupId) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ins = c.prepareStatement("INSERT OR REPLACE INTO StudentGroup(studentId, groupId) VALUES(?,?)")) {
            ins.setInt(1, studentId);
            ins.setInt(2, groupId);
            ins.executeUpdate();
        }
    }

    public static void assignSubjectToGroup(int subjectId, int groupId) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ins = c.prepareStatement("INSERT OR REPLACE INTO SubjectGroup(subjectId, groupId) VALUES(?,?)")) {
            ins.setInt(1, subjectId);
            ins.setInt(2, groupId);
            ins.executeUpdate();
        }
    }

    public static List<Student> listStudentsInGroup(int groupId) throws SQLException {
        List<Student> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT u.* FROM Users u JOIN StudentGroup sg ON u.id = sg.studentId WHERE sg.groupId = ?")) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new Student(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("login"), rs.getString("password")));
        }
        return list;
    }

    public static List<Subject> listSubjectsInGroup(int groupId) throws SQLException {
        List<Subject> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT s.* FROM Subjects s JOIN SubjectGroup sg ON s.id = sg.subjectId WHERE sg.groupId = ?")) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new Subject(rs.getInt("id"), rs.getString("name"), (rs.getObject("teacherId") != null) ? rs.getInt("teacherId") : null));
        }
        return list;
    }

    // Grades
    public static Grade upsertGrade(int studentId, int subjectId, String grade) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement up = c.prepareStatement("INSERT INTO Grades(studentId, subjectId, grade) VALUES(?,?,?) ON CONFLICT(studentId, subjectId) DO UPDATE SET grade=excluded.grade", Statement.RETURN_GENERATED_KEYS)) {
            up.setInt(1, studentId);
            up.setInt(2, subjectId);
            up.setString(3, grade);
            up.executeUpdate();
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM Grades WHERE studentId = ? AND subjectId = ?")) {
                ps.setInt(1, studentId);
                ps.setInt(2, subjectId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return new Grade(rs.getInt("id"), rs.getInt("studentId"), rs.getInt("subjectId"), rs.getString("grade"));
            }
        }
        return null;
    }

    public static List<Grade> listGradesForTeacher(int teacherId) throws SQLException {
        List<Grade> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT g.* FROM Grades g JOIN Subjects s ON g.subjectId = s.id WHERE s.teacherId = ?")) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Grade(rs.getInt("id"), rs.getInt("studentId"), rs.getInt("subjectId"), rs.getString("grade")));
        }
        return list;
    }

    public static List<Grade> listGradesForStudent(int studentId) throws SQLException {
        List<Grade> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT g.* FROM Grades g WHERE g.studentId = ?")) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Grade(rs.getInt("id"), rs.getInt("studentId"), rs.getInt("subjectId"), rs.getString("grade")));
        }
        return list;
    }
}
