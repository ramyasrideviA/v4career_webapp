package com.learn.learningarea.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "income")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate incomeDate;

    private String revenueChannel;

    private Double amount;

    private String paidBy;

    private String receivedBy;

    private String modeOfPayment;
}
