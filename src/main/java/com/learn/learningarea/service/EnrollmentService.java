package com.learn.learningarea.service;

import com.learn.learningarea.model.Enrollment;
import java.util.List;

public interface EnrollmentService {
    List<Enrollment> getAllEnrollments();

    List<Enrollment> searchEnrollments(String query);
    List<Enrollment> getLast32Days();

    Enrollment saveEnrollment(Enrollment enrollment);

    void deleteEnrollment(Long id);

    String generateNextEnrollmentId();

    Enrollment getEnrollmentByEnrollmentId(String enrollmentId);

    List<Enrollment> getReportsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
