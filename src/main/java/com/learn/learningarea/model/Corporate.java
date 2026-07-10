package com.learn.learningarea.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Corporate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String companyName;
    private String category;
    private String location;
    private String name;
    private String designation;
    private String mobileNo;
    private String emailId;
    private String website;
    private String tieUps;
    private String remarks;
    private LocalDate createdAt = LocalDate.now();
}
