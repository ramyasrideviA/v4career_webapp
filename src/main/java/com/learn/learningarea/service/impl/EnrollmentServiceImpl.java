package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.Enrollment;
import com.learn.learningarea.repository.EnrollmentRepository;
import com.learn.learningarea.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    @Override
    public List<Enrollment> getLast32Days() {
        return enrollmentRepository.findLast32Days(java.time.LocalDate.now().minusDays(32));
    }

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Enrollment saveEnrollment(Enrollment enrollment) {
        if (enrollment.getEnrollmentId() == null || enrollment.getEnrollmentId().isEmpty()) {
            enrollment.setEnrollmentId(generateNextEnrollmentId());
        }
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }

    @Override
    public List<Enrollment> searchEnrollments(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllEnrollments();
        }
        return enrollmentRepository.searchEnrollments(query);
    }

    @Override
    public String generateNextEnrollmentId() {
        // Get the last enrollment record
        Optional<Enrollment> lastEnrollment = enrollmentRepository.findTopByOrderByIdDesc();

        int nextId = 1;
        if (lastEnrollment.isPresent()) {
            String lastId = lastEnrollment.get().getEnrollmentId();
            if (lastId != null && lastId.startsWith("ENR-")) {
                try {
                    int lastNumber = Integer.parseInt(lastId.substring(4));
                    nextId = lastNumber + 1;
                } catch (NumberFormatException e) {
                    nextId = 1;
                }
            }
        }

        return String.format("ENR-%04d", nextId);
    }

    @Override
    public Enrollment getEnrollmentByEnrollmentId(String enrollmentId) {
        if (enrollmentId == null)
            return null;

        return enrollmentRepository
                .findByEnrollmentIdIgnoreCase(enrollmentId.trim())
                .orElse(null);
    }

    @Override
    public List<Enrollment> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return enrollmentRepository.findByEnrollmentDateBetween(startDate, endDate);
    }
}
