package com.learn.learningarea.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String employeeId;
    private String category;
    private String name;
    private String designation;
    private String mobileNo;
    private String emailId;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfJoining;
    
    private String salary;
    private String remarks;
    private LocalDate createdAt = LocalDate.now();
}
