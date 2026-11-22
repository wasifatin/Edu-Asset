package com.examsphere.service;

import com.examsphere.exception.AuthorizationException;
import com.examsphere.exception.InvalidApplicationException;
import com.examsphere.model.ApplicationRequest;
import com.examsphere.model.User;
import com.examsphere.storage.FileStorageService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing student grievances, recheck requests, examination retake submissions,
 * and administrative approval workflows.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class ApplicationService {

    private static ApplicationService instance;

    private final FileStorageService storage;
    private final List<ApplicationRequest> applications;

    private ApplicationService() {
        this.storage = FileStorageService.getInstance();
        this.applications = new ArrayList<>(storage.loadApplications());
        // Clean initial state: users input their own application requests
    }

    public static synchronized ApplicationService getInstance() {
        if (instance == null) {
            instance = new ApplicationService();
        }
        return instance;
    }

    private void seedInitialApplications() {
        ApplicationRequest a1 = new ApplicationRequest("APP-2026-001", "STU001", "RECHECK",
                "CSE221", "Requesting recheck for Midterm Question 3 mark calculation.");
        a1.setStatus("PENDING");

        ApplicationRequest a2 = new ApplicationRequest("APP-2026-002", "STU002", "MISSING_MARKS",
                "CSE222", "Assignment 2 submission was evaluated but not entered in system.");
        a2.setStatus("UNDER_REVIEW");
        a2.setAdminComments("Forwarded to course teacher Pranto Protim Choudhury.");

        applications.add(a1);
        applications.add(a2);
        storage.saveApplications(applications);
    }

    public synchronized void submitApplication(ApplicationRequest app) throws InvalidApplicationException {
        if (app == null) {
            throw new InvalidApplicationException("Application details cannot be null.");
        }
        if (app.getStudentId() == null || app.getStudentId().trim().isEmpty()) {
            throw new InvalidApplicationException("Student ID is required to submit an application.");
        }
        if (app.getDescription() == null || app.getDescription().trim().isEmpty()) {
            throw new InvalidApplicationException("Application description/reason cannot be blank.");
        }

        if (app.getApplicationId() == null || app.getApplicationId().trim().isEmpty()) {
            app.setApplicationId("APP-" + System.currentTimeMillis() % 100000);
        }
        app.setStatus("PENDING");
        app.setSubmissionDate(LocalDate.now().toString());
        app.setLastUpdated(LocalDate.now().toString());

        applications.add(app);
        storage.saveApplications(applications);
    }

    public synchronized void updateApplicationStatus(String appId, String status, String comments)
            throws AuthorizationException {
        User current = AuthService.getInstance().getCurrentUser();
        if (current == null || ("STUDENT".equalsIgnoreCase(current.getRole()))) {
            throw new AuthorizationException("Only administrative staff or coordinators can update application statuses.");
        }

        ApplicationRequest app = getApplicationById(appId);
        if (app != null) {
            app.setStatus(status);
            if (comments != null && !comments.trim().isEmpty()) {
                app.setAdminComments(comments);
            }
            app.setLastUpdated(LocalDate.now().toString());
            storage.saveApplications(applications);
        }
    }

    public synchronized ApplicationRequest getApplicationById(String appId) {
        if (appId == null) return null;
        for (ApplicationRequest a : applications) {
            if (a.getApplicationId().equalsIgnoreCase(appId.trim())) {
                return a;
            }
        }
        return null;
    }

    public synchronized List<ApplicationRequest> getAllApplications() {
        return new ArrayList<>(applications);
    }

    public synchronized List<ApplicationRequest> getApplicationsByStudent(String studentId) {
        if (studentId == null) return new ArrayList<>();
        return applications.stream()
                .filter(a -> studentId.equalsIgnoreCase(a.getStudentId()))
                .collect(Collectors.toList());
    }

    public synchronized List<ApplicationRequest> getApplicationsByStatus(String status) {
        if (status == null || "ALL".equalsIgnoreCase(status)) return getAllApplications();
        return applications.stream()
                .filter(a -> status.equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());
    }

    public synchronized List<ApplicationRequest> getPendingApplications() {
        return applications.stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus()) || "UNDER_REVIEW".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());
    }
}

