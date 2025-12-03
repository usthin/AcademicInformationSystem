package repos;

import db.DB;
import models.*;
import java.sql.*;

public class UserRepo {

    public static User findByLogin(String login) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM Users WHERE login = ?")) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String fn = rs.getString("firstName");
                String ln = rs.getString("lastName");
                String pw = rs.getString("password");
                String role = rs.getString("role");
                if ("ADMIN".equals(role)) return new Administrator(id, fn, ln, login, pw);
                if ("TEACHER".equals(role)) return new Teacher(id, fn, ln, login, pw);
                if ("STUDENT".equals(role)) return new Student(id, fn, ln, login, pw);
            }
        }
        return null;
    }

    public static Administrator createAdminIfNotExists() throws SQLException {
        try (Connection c = DB.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT u.* FROM Users u WHERE u.role = 'ADMIN' LIMIT 1")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return new Administrator(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"),
                            rs.getString("login"), rs.getString("password"));
                }
            }
            try (PreparedStatement ins = c.prepareStatement("INSERT INTO Users(firstName,lastName,login,password,role) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
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
                    return new Administrator(id, "System", "Administrator", "admin", "admin");
                }
            }
        }
        return null;
    }
}
