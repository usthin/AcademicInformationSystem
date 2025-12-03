package ui;

import models.*;
import repos.AcademicRepo;
import repos.UserRepo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class Forms {

    public static void showLoginForm() {
        JFrame frame = new JFrame("Academic System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(380, 200);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JTextField loginField = new JTextField();
        JPasswordField pw = new JPasswordField();

        form.add(new JLabel("Login:"));
        form.add(loginField);
        form.add(new JLabel("Password:"));
        form.add(pw);

        JComboBox<String> role = new JComboBox<>(new String[]{"ADMIN", "TEACHER", "STUDENT"});
        form.add(new JLabel("Role:"));
        form.add(role);

        JPanel bottom = new JPanel();
        JButton bLogin = new JButton("Login");
        JButton bExit = new JButton("Exit");
        bottom.add(bLogin);
        bottom.add(bExit);

        bLogin.addActionListener(a -> {
            String l = loginField.getText().trim();
            String p = new String(pw.getPassword());
            String r = (String) role.getSelectedItem();
            if (l.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Enter login and password.");
                return;
            }
            try {
                User u = UserRepo.findByLogin(l);
                if (u == null) {
                    JOptionPane.showMessageDialog(frame, "Unknown login.");
                    return;
                }
                if (!u.getPassword().equals(p)) {
                    JOptionPane.showMessageDialog(frame, "Incorrect password.");
                    return;
                }
                if (!u.getRole().equals(r)) {
                    JOptionPane.showMessageDialog(frame, "Role mismatch.");
                    return;
                }
                frame.dispose();
                if ("ADMIN".equals(r)) showAdminPanel((Administrator) u);
                else if ("TEACHER".equals(r)) showTeacherPanel((Teacher) u);
                else showStudentPanel((Student) u);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Login error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        bExit.addActionListener(a -> System.exit(0));
        frame.setLayout(new BorderLayout());
        frame.add(form, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Admin panel
    public static void showAdminPanel(Administrator admin) {
        JFrame frame = new JFrame("Admin - " + admin.getFirstName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(920, 600);

        JPanel top = new JPanel();
        JButton bTeachers = new JButton("Manage Teachers");
        JButton bStudents = new JButton("Manage Students");
        JButton bSubjects = new JButton("Manage Subjects");
        JButton bGroups = new JButton("Manage Groups");
        JButton bAssign = new JButton("Assignments");
        JButton bLogout = new JButton("Logout");
        top.add(bTeachers);
        top.add(bStudents);
        top.add(bSubjects);
        top.add(bGroups);
        top.add(bAssign);
        top.add(bLogout);

        JTextArea log = new JTextArea();
        log.setEditable(false);

        frame.setLayout(new BorderLayout());
        frame.add(top, BorderLayout.NORTH);
        frame.add(new JScrollPane(log), BorderLayout.CENTER);

        bTeachers.addActionListener(e -> manageTeachersDialog(frame));
        bStudents.addActionListener(e -> manageStudentsDialog(frame));
        bSubjects.addActionListener(e -> manageSubjectsDialog(frame));
        bGroups.addActionListener(e -> manageGroupsDialog(frame));
        bAssign.addActionListener(e -> assignmentsDialog(frame));
        bLogout.addActionListener(e -> {
            frame.dispose();
            showLoginForm();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Manage teachers
    private static void manageTeachersDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Teachers", true);
        d.setSize(600, 400);
        d.setLayout(new BorderLayout());
        DefaultTableModel tm = new DefaultTableModel(new Object[]{"ID", "First", "Last", "Login"}, 0);
        JTable table = new JTable(tm);
        try {
            for (Teacher t : AcademicRepo.listTeachers())
                tm.addRow(new Object[]{t.getId(), t.getFirstName(), t.getLastName(), t.getLogin()});
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(d, ex.getMessage());
        }
        JPanel bottom = new JPanel();
        JTextField fn = new JTextField(8), ln = new JTextField(8);
        JButton add = new JButton("Add"), del = new JButton("Delete Selected");
        bottom.add(new JLabel("First:"));
        bottom.add(fn);
        bottom.add(new JLabel("Last:"));
        bottom.add(ln);
        bottom.add(add);
        bottom.add(del);

        add.addActionListener(a -> {
            String first = fn.getText().trim(), last = ln.getText().trim();
            if (first.isEmpty() || last.isEmpty()) { JOptionPane.showMessageDialog(d, "Missing"); return; }
            try {
                Teacher t = AcademicRepo.createTeacher(first, last);
                tm.addRow(new Object[]{t.getId(), t.getFirstName(), t.getLastName(), t.getLogin()});
                fn.setText(""); ln.setText("");
                JOptionPane.showMessageDialog(d, "Created: login=" + t.getLogin() + ", pw=" + t.getPassword());
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });

        del.addActionListener(a -> {
            int s = table.getSelectedRow();
            if (s < 0) { JOptionPane.showMessageDialog(d, "Select"); return; }
            int id = (int) tm.getValueAt(s, 0);
            try {
                AcademicRepo.deleteUserById(id);
                tm.removeRow(s);
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });

        d.add(new JScrollPane(table), BorderLayout.CENTER);
        d.add(bottom, BorderLayout.SOUTH);
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }

    // Manage students
    private static void manageStudentsDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Students", true);
        d.setSize(700, 420);
        d.setLayout(new BorderLayout());
        DefaultTableModel tm = new DefaultTableModel(new Object[]{"ID", "First", "Last", "Login"}, 0);
        JTable table = new JTable(tm);
        try {
            for (Student s : AcademicRepo.listStudents())
                tm.addRow(new Object[]{s.getId(), s.getFirstName(), s.getLastName(), s.getLogin()});
        } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        JPanel bottom = new JPanel();
        JTextField fn = new JTextField(8), ln = new JTextField(8);
        JButton add = new JButton("Add"), del = new JButton("Delete Selected");
        bottom.add(new JLabel("First:"));
        bottom.add(fn);
        bottom.add(new JLabel("Last:"));
        bottom.add(ln);
        bottom.add(add);
        bottom.add(del);

        add.addActionListener(a -> {
            String first = fn.getText().trim(), last = ln.getText().trim();
            if (first.isEmpty() || last.isEmpty()) { JOptionPane.showMessageDialog(d, "Missing"); return; }
            try {
                Student s = AcademicRepo.createStudent(first, last);
                tm.addRow(new Object[]{s.getId(), s.getFirstName(), s.getLastName(), s.getLogin()});
                fn.setText(""); ln.setText("");
                JOptionPane.showMessageDialog(d, "Created: login=" + s.getLogin() + ", pw=" + s.getPassword());
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });

        del.addActionListener(a -> {
            int s = table.getSelectedRow();
            if (s < 0) { JOptionPane.showMessageDialog(d, "Select"); return; }
            int id = (int) tm.getValueAt(s, 0);
            try {
                AcademicRepo.deleteUserById(id);
                tm.removeRow(s);
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });

        d.add(new JScrollPane(table), BorderLayout.CENTER);
        d.add(bottom, BorderLayout.SOUTH);
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }

    // Manage subjects
    private static void manageSubjectsDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Subjects", true);
        d.setSize(700, 420);
        d.setLayout(new BorderLayout());
        DefaultTableModel tm = new DefaultTableModel(new Object[]{"ID", "Name", "TeacherId"}, 0);
        JTable table = new JTable(tm);
        try {
            for (Subject s : AcademicRepo.listSubjects())
                tm.addRow(new Object[]{s.id, s.name, s.teacherId});
        } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        JPanel bottom = new JPanel();
        JTextField name = new JTextField(12);
        JButton add = new JButton("Add"), del = new JButton("Delete Selected");
        bottom.add(new JLabel("Name:"));
        bottom.add(name);
        bottom.add(add);
        bottom.add(del);

        add.addActionListener(a -> {
            String n = name.getText().trim();
            if (n.isEmpty()) { JOptionPane.showMessageDialog(d, "Missing"); return; }
            try {
                Subject s = AcademicRepo.createSubject(n);
                tm.addRow(new Object[]{s.id, s.name, s.teacherId});
                name.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });

        del.addActionListener(a -> {
            int s = table.getSelectedRow();
            if (s < 0) { JOptionPane.showMessageDialog(d, "Select"); return; }
            int id = (int) tm.getValueAt(s, 0);
            try {
                AcademicRepo.deleteSubject(id);
                tm.removeRow(s);
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });

        d.add(new JScrollPane(table), BorderLayout.CENTER);
        d.add(bottom, BorderLayout.SOUTH);
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }

    // Manage groups
    private static void manageGroupsDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Groups", true);
        d.setSize(700, 420);
        d.setLayout(new BorderLayout());
        DefaultTableModel tm = new DefaultTableModel(new Object[]{"ID", "Name"}, 0);
        JTable table = new JTable(tm);
        try {
            for (GroupModel g : AcademicRepo.listGroups())
                tm.addRow(new Object[]{g.id, g.name});
        } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        JPanel bottom = new JPanel();
        JTextField name = new JTextField(12);
        JButton add = new JButton("Add");
        bottom.add(new JLabel("Name:"));
        bottom.add(name);
        bottom.add(add);

        add.addActionListener(a -> {
            String n = name.getText().trim();
            if (n.isEmpty()) { JOptionPane.showMessageDialog(d, "Missing"); return; }
            try {
                GroupModel g = AcademicRepo.createGroup(n);
                tm.addRow(new Object[]{g.id, g.name});
                name.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });

        d.add(new JScrollPane(table), BorderLayout.CENTER);
        d.add(bottom, BorderLayout.SOUTH);
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }

    // Assignments dialog (teachers->subjects, students->groups, subjects->groups)
    private static void assignmentsDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Assignments", true);
        d.setSize(800, 520);
        d.setLayout(new BorderLayout());
        JPanel top = new JPanel();
        JButton b1 = new JButton("Assign Teacher to Subject");
        JButton b2 = new JButton("Assign Student to Group");
        JButton b3 = new JButton("Assign Subject to Group");
        top.add(b1);
        top.add(b2);
        top.add(b3);
        d.add(top, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        d.add(new JScrollPane(area), BorderLayout.CENTER);

        b1.addActionListener(a -> {
            try {
                List<Subject> subs = AcademicRepo.listSubjects();
                if (subs.isEmpty()) { JOptionPane.showMessageDialog(d, "No subjects"); return; }
                String subj = (String) JOptionPane.showInputDialog(d, "Select subject", "Subject", JOptionPane.PLAIN_MESSAGE, null, subs.stream().map(Object::toString).toArray(), null);
                if (subj == null) return;
                int sid = Integer.parseInt(subj.split(":")[0]);

                List<Teacher> teachers = AcademicRepo.listTeachers();
                String tsel = (String) JOptionPane.showInputDialog(d, "Select teacher (cancel to unassign)", "Teacher", JOptionPane.PLAIN_MESSAGE, null, teachers.stream().map(Object::toString).toArray(), null);
                Integer tid = null;
                if (tsel != null) tid = Integer.parseInt(tsel.split(":")[0]);

                AcademicRepo.assignTeacherToSubject(sid, tid);
                area.append("Assigned teacher " + (tid == null ? "(none)" : tsel) + " to subject " + subj + "\n");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });

        b2.addActionListener(a -> {
            try {
                List<Student> studs = AcademicRepo.listStudents();
                if (studs.isEmpty()) { JOptionPane.showMessageDialog(d, "No students"); return; }
                String ssel = (String) JOptionPane.showInputDialog(d, "Select student", "Student", JOptionPane.PLAIN_MESSAGE, null, studs.stream().map(Object::toString).toArray(), null);
                if (ssel == null) return;
                int sid = Integer.parseInt(ssel.split(":")[0]);

                List<GroupModel> groups = AcademicRepo.listGroups();
                if (groups.isEmpty()) { JOptionPane.showMessageDialog(d, "No groups"); return; }
                String gsel = (String) JOptionPane.showInputDialog(d, "Select group", "Group", JOptionPane.PLAIN_MESSAGE, null, groups.stream().map(Object::toString).toArray(), null);
                if (gsel == null) return;
                int gid = Integer.parseInt(gsel.split(":")[0]);

                AcademicRepo.assignStudentToGroup(sid, gid);
                area.append("Assigned student " + ssel + " to group " + gsel + "\n");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });

        b3.addActionListener(a -> {
            try {
                List<Subject> subs = AcademicRepo.listSubjects();
                if (subs.isEmpty()) { JOptionPane.showMessageDialog(d, "No subjects"); return; }
                String subj = (String) JOptionPane.showInputDialog(d, "Select subject", "Subject", JOptionPane.PLAIN_MESSAGE, null, subs.stream().map(Object::toString).toArray(), null);
                if (subj == null) return;
                int sid = Integer.parseInt(subj.split(":")[0]);

                List<GroupModel> groups = AcademicRepo.listGroups();
                if (groups.isEmpty()) { JOptionPane.showMessageDialog(d, "No groups"); return; }
                String gsel = (String) JOptionPane.showInputDialog(d, "Select group", "Group", JOptionPane.PLAIN_MESSAGE, null, groups.stream().map(Object::toString).toArray(), null);
                if (gsel == null) return;
                int gid = Integer.parseInt(gsel.split(":")[0]);

                AcademicRepo.assignSubjectToGroup(sid, gid);
                area.append("Assigned subject " + subj + " to group " + gsel + "\n");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });

        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }

    // Teacher panel
    public static void showTeacherPanel(Teacher teacher) {
        JFrame frame = new JFrame("Teacher - " + teacher.getFirstName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        JPanel top = new JPanel();
        JButton bEnter = new JButton("Enter/Edit Grade");
        JButton bView = new JButton("View My Students' Grades");
        JButton bLogout = new JButton("Logout");
        top.add(bEnter);
        top.add(bView);
        top.add(bLogout);

        DefaultTableModel tm = new DefaultTableModel(new Object[]{"StudentID", "Student", "SubjectID", "Subject", "Grade"}, 0);
        JTable table = new JTable(tm);

        frame.setLayout(new BorderLayout());
        frame.add(top, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        bEnter.addActionListener(a -> {
            try {
                List<Subject> subs = AcademicRepo.listSubjects();
                List<Subject> mine = new java.util.ArrayList<>();
                for (Subject s : subs) if (s.teacherId != null && s.teacherId == teacher.getId()) mine.add(s);
                if (mine.isEmpty()) { JOptionPane.showMessageDialog(frame, "You have no subjects"); return; }
                String subj = (String) JOptionPane.showInputDialog(frame, "Select subject", "Subject", JOptionPane.PLAIN_MESSAGE, null, mine.stream().map(Object::toString).toArray(), null);
                if (subj == null) return;
                int subjId = Integer.parseInt(subj.split(":")[0]);

                // gather students for that subject (via groups). If none, fallback to all students.
                List<Student> studs = AcademicRepo.listStudentsInGroup(subjId); // note: subject-group mapping may be used
                if (studs.isEmpty()) studs = AcademicRepo.listStudents();

                String ssel = (String) JOptionPane.showInputDialog(frame, "Select student", "Student", JOptionPane.PLAIN_MESSAGE, null, studs.stream().map(Object::toString).toArray(), null);
                if (ssel == null) return;
                int studId = Integer.parseInt(ssel.split(":")[0]);

                String grade = JOptionPane.showInputDialog(frame, "Enter grade (A,8,Passed):");
                if (grade == null || grade.trim().isEmpty()) { JOptionPane.showMessageDialog(frame, "Grade empty"); return; }

                AcademicRepo.upsertGrade(studId, subjId, grade.trim());
                JOptionPane.showMessageDialog(frame, "Saved");
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage()); }
        });

        bView.addActionListener(a -> {
            try {
                tm.setRowCount(0);
                for (models.Grade g : AcademicRepo.listGradesForTeacher(teacher.getId())) {
                    Subject s = AcademicRepo.listSubjects().stream().filter(x -> x.id == g.subjectId).findFirst().orElse(null);
                    Student st = AcademicRepo.listStudents().stream().filter(x -> x.getId() == g.studentId).findFirst().orElse(null);
                    tm.addRow(new Object[]{g.studentId, st == null ? "(unknown)" : st.getFirstName() + " " + st.getLastName(), g.subjectId, s == null ? "(unknown)" : s.name, g.grade});
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage()); }
        });

        bLogout.addActionListener(a -> {
            frame.dispose();
            showLoginForm();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Student panel
    public static void showStudentPanel(Student student) {
        JFrame frame = new JFrame("Student - " + student.getFirstName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        JPanel top = new JPanel();
        JButton bRef = new JButton("Refresh Grades");
        JButton bLogout = new JButton("Logout");
        top.add(bRef);
        top.add(bLogout);

        DefaultTableModel tm = new DefaultTableModel(new Object[]{"SubjectID", "Subject", "Grade", "TeacherID"}, 0);
        JTable table = new JTable(tm);

        frame.setLayout(new BorderLayout());
        frame.add(top, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        bRef.addActionListener(a -> {
            try {
                tm.setRowCount(0);
                for (models.Grade g : AcademicRepo.listGradesForStudent(student.getId())) {
                    Subject s = AcademicRepo.listSubjects().stream().filter(x -> x.id == g.subjectId).findFirst().orElse(null);
                    tm.addRow(new Object[]{g.subjectId, s == null ? "(unknown)" : s.name, g.grade, s == null ? null : s.teacherId});
                }
                if (tm.getRowCount() == 0) JOptionPane.showMessageDialog(frame, "No grades yet.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage()); }
        });

        bLogout.addActionListener(a -> {
            frame.dispose();
            showLoginForm();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
