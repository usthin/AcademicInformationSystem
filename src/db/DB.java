package db;

import java.sql.*;

public class DB {
    private static final String DB_URL = "jdbc:sqlite:academic_standard.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void init() throws SQLException {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
            st.execute("CREATE TABLE IF NOT EXISTS Users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "firstName TEXT NOT NULL," +
                    "lastName TEXT NOT NULL," +
                    "login TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL" +
                    ");");
            st.execute("CREATE TABLE IF NOT EXISTS Students (id INTEGER PRIMARY KEY, FOREIGN KEY(id) REFERENCES Users(id) ON DELETE CASCADE);");
            st.execute("CREATE TABLE IF NOT EXISTS Teachers (id INTEGER PRIMARY KEY, FOREIGN KEY(id) REFERENCES Users(id) ON DELETE CASCADE);");
            st.execute("CREATE TABLE IF NOT EXISTS Admins (id INTEGER PRIMARY KEY, FOREIGN KEY(id) REFERENCES Users(id) ON DELETE CASCADE);");
            st.execute("CREATE TABLE IF NOT EXISTS Subjects (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "teacherId INTEGER NULL," +
                    "FOREIGN KEY(teacherId) REFERENCES Teachers(id) ON DELETE SET NULL" +
                    ");");
            st.execute("CREATE TABLE IF NOT EXISTS GroupsTable (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL UNIQUE);");
            st.execute("CREATE TABLE IF NOT EXISTS StudentGroup (" +
                    "studentId INTEGER," +
                    "groupId INTEGER," +
                    "PRIMARY KEY(studentId, groupId)," +
                    "FOREIGN KEY(studentId) REFERENCES Students(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(groupId) REFERENCES GroupsTable(id) ON DELETE CASCADE);");
            st.execute("CREATE TABLE IF NOT EXISTS SubjectGroup (" +
                    "subjectId INTEGER," +
                    "groupId INTEGER," +
                    "PRIMARY KEY(subjectId, groupId)," +
                    "FOREIGN KEY(subjectId) REFERENCES Subjects(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(groupId) REFERENCES GroupsTable(id) ON DELETE CASCADE);");
            st.execute("CREATE TABLE IF NOT EXISTS Grades (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "studentId INTEGER NOT NULL," +
                    "subjectId INTEGER NOT NULL," +
                    "grade TEXT NOT NULL," +
                    "FOREIGN KEY(studentId) REFERENCES Students(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(subjectId) REFERENCES Subjects(id) ON DELETE CASCADE," +
                    "UNIQUE(studentId, subjectId)" +
                    ");");

            // Ensure default admin exists
            try (PreparedStatement ps = c.prepareStatement("SELECT id FROM Users WHERE role='ADMIN' LIMIT 1")) {
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    try (PreparedStatement ins = c.prepareStatement(
                            "INSERT INTO Users(firstName,lastName,login,password,role) VALUES(?,?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS)) {
                        ins.setString(1, "System");
                        ins.setString(2, "Administrator");
                        ins.setString(3, "admin");
                        ins.setString(4, "admin");
                        ins.setString(5, "ADMIN");
                        ins.executeUpdate();
                        ResultSet gk = ins.getGeneratedKeys();
                        if (gk.next()) {
                            int id = gk.getInt(1);
                            try (PreparedStatement ins2 = c.prepareStatement("INSERT INTO Admins(id) VALUES(?)")) {
                                ins2.setInt(1, id);
                                ins2.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
    }
}
