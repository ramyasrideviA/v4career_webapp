package com.learn.learningarea.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class College {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String collegeName;
    private String category;
    private String location;
    private String contactPerson;
    private String name;
    private String mobileNo;
    private String emailId;
    private String website;
    private String strength;
    private String weakness;
    private String tieUps;
    private String remarks;
    private LocalDate createdAt = LocalDate.now();
}
