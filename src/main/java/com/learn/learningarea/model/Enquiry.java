package com.learn.learningarea.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "enquiry")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    
    private LocalDate enquiryDate;

    private String service;

    private String name;

    private String mobileNumber;

    private String emailId;

    private String location;

    private String category;

    private String handledBy;

    private String remarks;
}
