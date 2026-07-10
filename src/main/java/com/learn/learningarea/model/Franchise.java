package com.learn.learningarea.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Franchise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String franchiseName;
    private String emailId;
    private String mobileNumber;
    private String address;
    private String country;
    private String logoPath;
}
