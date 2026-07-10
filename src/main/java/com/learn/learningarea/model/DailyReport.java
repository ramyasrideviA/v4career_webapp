package com.learn.learningarea.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "daily_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reportDate;

    private Integer enquiries;

    private Integer enrollment;

    private Double revenue;
}
