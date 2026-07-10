package com.learn.learningarea.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String enrollmentId;
    private String name;
    private String mobileNo;
    private String emailId;
    private String amount;
    private String category;
    private int installments;
    private String nextInstallmentDate;
    private String paymentStatus;
    private LocalDate createdAt = LocalDate.now();

    @Column(nullable = false)
    private Boolean reminderSent7Days = false;

    @Column(nullable = false)
    private Boolean reminderSent3Days = false;
}
