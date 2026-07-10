package com.learn.learningarea.service;

import com.learn.learningarea.model.Enquiry;
import java.util.List;
import java.util.Optional;

public interface EnquiryService {
    Enquiry saveEnquiry(Enquiry enquiry);

    List<Enquiry> getAllEnquiries();

    List<Enquiry> searchEnquiries(String query);

    List<Enquiry> getLast32Days();

    Optional<Enquiry> getEnquiryById(Long id);

    void deleteEnquiry(Long id);

    List<Enquiry> getReportsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
