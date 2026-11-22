package com.examsphere.service;

import com.examsphere.exception.AuthorizationException;
import com.examsphere.exception.DuplicateStudentIdException;
import com.examsphere.exception.InvalidLoginException;
import com.examsphere.model.*;
import com.examsphere.storage.FileStorageService;
import com.examsphere.util.PasswordUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Service managing user authentication, role-based access control, session state,
 * and user lifecycle operations.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class AuthService {

    private static AuthService instance;

    private final FileStorageService storage;
    private final List<User> users;
    private User currentUser;

    private AuthService() {
        this.storage = FileStorageService.getInstance();
        this.users = new ArrayList<>(storage.loadUsers());
        if (this.users.isEmpty()) {
            initializeDefaultUsers();
        }
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Authenticates a user with given credentials.
     *
     * @param userId   user ID
     * @param password plain text password
     * @return authenticated User instance
     * @throws InvalidLoginException if user not found, account inactive, or password invalid
     */
    public synchronized User login(String userId, String password) throws InvalidLoginException {
        if (userId == null || userId.trim().isEmpty() || password == null) {
            throw new InvalidLoginException("User ID and password must not be empty.");
        }

        User user = getUserById(userId.trim());
        if (user == null) {
            throw new InvalidLoginException("Invalid user ID or user does not exist.");
        }

        if (!user.isActive()) {
            throw new InvalidLoginException("Account is deactivated. Please contact Administration.");
        }

        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new InvalidLoginException("Invalid password. Please verify and try again.");
        }

        this.currentUser = user;
        return user;
    }

    public synchronized void logout() {
        this.currentUser = null;
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks if the currently logged-in user has permission for a specific action.
     *
     * @param action permission identifier
     * @return true if permitted, false otherwise
     */
    public synchronized boolean hasPermission(String action) {
        if (currentUser == null) return false;
        String role = currentUser.getRole();

        if ("ADMIN".equals(role)) {
            return true; // Admin has full access
        }

        switch (action) {
            case "MANAGE_STUDENTS":
            case "VIEW_REPORTS":
                return "COORDINATOR".equals(role);
            case "ENTER_MARKS":
            case "MARK_ATTENDANCE":
                return "COORDINATOR".equals(role) || "TEACHER".equals(role);
            case "VIEW_OWN_MARKS":
            case "SUBMIT_APPLICATION":
                return "STUDENT".equals(role);
            case "SCHEDULE_EXAM":
                return "COORDINATOR".equals(role);
            case "PUBLISH_RESULTS":
                return false; // only admin
            default:
                return false;
        }
    }

    public synchronized void requirePermission(String action) throws AuthorizationException {
        if (!hasPermission(action)) {
            throw new AuthorizationException("Access denied: You do not possess permission for action [" + action + "].");
        }
    }

    public synchronized void createUser(User user) throws DuplicateStudentIdException {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        if (getUserById(user.getUserId()) != null) {
            throw new DuplicateStudentIdException("A user with ID '" + user.getUserId() + "' already exists.");
        }

        users.add(user);
        storage.saveUsers(users);
    }

    public synchronized void updateUser(User user) {
        if (user == null) return;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equalsIgnoreCase(user.getUserId())) {
                users.set(i, user);
                storage.saveUsers(users);
                if (currentUser != null && currentUser.getUserId().equalsIgnoreCase(user.getUserId())) {
                    currentUser = user;
                }
                return;
            }
        }
    }

    public synchronized void deactivateUser(String userId) {
        User user = getUserById(userId);
        if (user != null) {
            user.setActive(false);
            updateUser(user);
        }
    }

    public synchronized void activateUser(String userId) {
        User user = getUserById(userId);
        if (user != null) {
            user.setActive(true);
            updateUser(user);
        }
    }

    public synchronized List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public synchronized User getUserById(String userId) {
        if (userId == null) return null;
        for (User u : users) {
            if (u.getUserId().equalsIgnoreCase(userId.trim())) {
                return u;
            }
        }
        return null;
    }

    /**
     * Initializes default demo users if the user database is empty.
     */
    public synchronized void initializeDefaultUsers() {
        users.clear();

        // 1. Examination Controller (Admin)
        Admin admin = new Admin("ADMIN001", "Mr. Md. Ashraful Islam Talukder", "ashrafulislam.cse@diu.edu.bd",
                PasswordUtil.hashPassword("admin123"), "Examination Controller");

        // 2. Department Coordinator
        DepartmentCoordinator coord = new DepartmentCoordinator("COORD001", "Dewan Mamun Raza",
                "dewan.cse@diu.edu.bd", PasswordUtil.hashPassword("coord123"), "CSE");

        // 3. Faculty Teacher / Invigilator
        Teacher teacher = new Teacher("TCH001", "Pranto Protim Choudhury", "prantoprotimchoudhury.cse@diu.edu.bd",
                PasswordUtil.hashPassword("teacher123"), "CSE", "Faculty / Lecturer");
        teacher.addAssignedCourse("CSE221");

        // 4. Student
        Student student = new Student("STU001", "Fatin Sharhad", "252-15-869@diu.edu.bd",
                PasswordUtil.hashPassword("student123"), "CSE", "B.Sc. in CSE", 5, "+8801711223344");

        // 5. Additional Demo Student for testing
        Student student2 = new Student("STU002", "Nusrat Jahan", "nusrat.stu@examsphere.diu.edu.bd",
                PasswordUtil.hashPassword("student123"), "CSE", "B.Sc. in CSE", 5, "+8801722334455");

        users.add(admin);
        users.add(coord);
        users.add(teacher);
        users.add(student);
        users.add(student2);

        storage.saveUsers(users);
    }
}

