package com.learn.learningarea.repository;

import com.learn.learningarea.model.Employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT em FROM Employee em WHERE em.dateOfJoining >= :startDate ORDER BY em.dateOfJoining DESC")
    List<Employee> findLast32Days(@Param("startDate") LocalDate startDate);

    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.designation) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.mobileNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.emailId) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Employee> searchEmployees(@Param("query") String query);

    List<Employee> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);
}
