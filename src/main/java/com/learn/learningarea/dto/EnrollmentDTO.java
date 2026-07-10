package com.learn.learningarea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private String name;
    private String mobileNo;
    private String emailId;
    private String amount;
}
