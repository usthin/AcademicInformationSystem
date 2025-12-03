# AcademicInformationSystem
A lightweight Academic Information System (AIS) prototype built with Java and Swing (Windows Forms). Implements Role-Based Access Control (RBAC) for Administrators, Teachers, and Students using a 3-tier architecture and strict OOP principles. Powered by SQLite.

# Academic Information System (AIS) Prototype

A desktop application prototype designed to manage academic evaluations, distinct user roles, and study data. This project demonstrates a **3-Tier Architecture** (UI, Logic, Data) without the use of high-level frameworks like Spring, relying instead on core Java principles and JDBC.

## 📋 Project Purpose
The system allows an educational institution to manage the grading process digitally. It enforces strict separation of concerns based on user roles:
* **Administrators** manage the infrastructure (Users, Groups, Subjects).
* **Teachers** manage the academic performance (Grading).
* **Students** view their results.

## 🛠 Tech Stack & Requirements
* **Language:** Java (JDK 17+)
* **GUI:** Java Swing (JFrame, JPanel, JOptionPane)
* **Database:** SQLite (via JDBC)
* **Architecture:** DAO Pattern (Data Access Object)
* **Constraints:** Pure Java (No Spring/Hibernate), Manual Object-Mapping.

## ✨ Key Features

### 1. Role-Based Access Control (RBAC)
The system automatically detects the user role upon login and renders the appropriate dashboard:
* **Administrator:** Full CRUD rights for Users (Students/Teachers), Groups, and Subjects.
* **Teacher:** Can view assigned subjects and **enter/edit grades** for students in those groups.
* **Student:** Read-only access to their own grades.

### 2. Login Logic
* **Username:** User's First Name.
* **Password:** User's Last Name (automatically generated logic).
* **Validation:** Database checked for role and credentials.

### 3. Object-Oriented Design
This project strictly adheres to OOP principles:
* **Encapsulation:** All data models (`Student`, `Grade`) utilize private fields with getters/setters.
* **Inheritance:** `Admin`, `Teacher`, and `Student` classes all inherit from the abstract `User` class.
* **Polymorphism:** The dashboard generation and certain behaviors are overridden based on the subclass.
* **Abstraction:** Data access is handled via Interfaces (`IUserRepository`, `IGradeRepository`) to decouple the database from the UI.

## 🗄️ Database Schema (ERD)
The application uses a relational database with the following structure:

* **Groups:** Stores class identifiers (e.g., "CS-2024").
* **Users:** Stores all actors. Column `role` differentiates Admin/Teacher/Student.
* **Subjects:** Connects a Subject Name to a specific Teacher.
* **Grades:** Links a Student, a Subject, and a numeric Value.

## 🚀 How to Run

1.  **Clone the repository**
    ```bash
    git clone (https://github.com/usthin/AcademicInformationSystem.git)
    ```
2.  **Open in IDE**
    Import the project into IntelliJ IDEA or Eclipse as a Java Project.
3.  **Dependencies**
    Ensure the `sqlite-jdbc` JAR file is added to your project's Build Path / Libraries.
4.  **Database Setup**
    * The application looks for `academic.db` in the root folder.
    * If running for the first time, use the `DatabaseSetup.java` utility (if provided) or run the SQL scripts in `/sql/schema.sql`.
5.  **Run**
    Execute the `Main.java` file to launch the Login Window.

## 👤 Default Login Credentials
*(For testing purposes)*

| Role | Username (First Name) | Password (Last Name) |
| :--- | :--- | :--- |
| **Admin** | `admin` | `admin` |
| **Teacher** | `John` | `Doe` |
| **Student** | `Alice` | `Smith` |

## 📂 Project Structure

```text
src/
├── model/          # POJOs (User, Student, Grade)
├── dao/            # Data Access Interfaces & Implementations
├── ui/             # Swing Forms (LoginFrame, Dashboards)
├── util/           # DB Connection Helper
└── Main.java       # Entry point
