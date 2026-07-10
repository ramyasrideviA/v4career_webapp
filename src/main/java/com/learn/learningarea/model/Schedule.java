package com.learn.learningarea.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String batchType;
    private String batchNo;
    private String programName;
    private String studentCount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @JsonFormat(pattern = "hh:mm a")
    @DateTimeFormat(pattern = "h a")
    private LocalTime startTime;
    @JsonFormat(pattern = "hh:mm a")
    @DateTimeFormat(pattern = "h a")
    private LocalTime endTime;

    private String remarks;
}
