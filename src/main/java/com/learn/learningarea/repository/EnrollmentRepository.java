package com.learn.learningarea.repository;

import com.learn.learningarea.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT en FROM Enrollment en WHERE en.enrollmentDate >= :startDate ORDER BY en.enrollmentDate DESC")
    List<Enrollment> findLast32Days(@Param("startDate") java.time.LocalDate startDate);

    Optional<Enrollment> findTopByOrderByIdDesc();

    Optional<Enrollment> findByEnrollmentIdIgnoreCase(String enrollmentId);

    @Query("SELECT en FROM Enrollment en WHERE " +
            "LOWER(en.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(en.mobileNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(en.service) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(en.emailId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(en.enrollmentId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(en.onboarding) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Enrollment> searchEnrollments(@Param("query") String query);

    List<Enrollment> findByEnrollmentDateBetween(LocalDate startDate, LocalDate endDate);
}
