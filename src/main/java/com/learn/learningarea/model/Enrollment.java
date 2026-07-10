package com.learn.learningarea.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate enrollmentDate;

    private String name;

    private String mobileNo;

    private String emailId;

    private String service;

    @Column(unique = true)
    private String enrollmentId;

    private String amount;

    private LocalDate durationFrom;

    private LocalDate durationTo;

    private String onboarding;

    private String remarks;
}
