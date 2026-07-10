package com.learn.learningarea.repository;

import com.learn.learningarea.model.Payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :startDate ORDER BY p.createdAt DESC")
    List<Payment> findLast32Days(@Param("startDate") LocalDate startDate);

    @Query("SELECT p FROM Payment p WHERE " +
            "LOWER(p.enrollmentId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.mobileNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.emailId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(p.amount AS string) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.nextInstallmentDate) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.paymentStatus) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Payment> searchPayments(@Param("query") String query);

    List<Payment> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM Payment p WHERE p.nextInstallmentDate IS NOT NULL")
    List<Payment> findAllWithNextInstallmentDate();
}
