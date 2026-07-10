package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.Enquiry;
import com.learn.learningarea.repository.EnquiryRepository;
import com.learn.learningarea.service.EnquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Service
public class EnquiryServiceImpl implements EnquiryService {
    @Override
    public List<Enquiry> getLast32Days() {
        return enquiryRepository.findLast32Days(java.time.LocalDate.now().minusDays(32));
    }

    @Autowired
    private EnquiryRepository enquiryRepository;

    @Override
    public Enquiry saveEnquiry(Enquiry enquiry) {
        return enquiryRepository.save(enquiry);
    }

    @Override
    public List<Enquiry> getAllEnquiries() {
        return enquiryRepository.findAll();
    }

    @Override
    public List<Enquiry> searchEnquiries(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllEnquiries();
        }
        return enquiryRepository.searchEnquiries(query);
    }

    @Override
    public Optional<Enquiry> getEnquiryById(Long id) {
        return enquiryRepository.findById(id);
    }

    @Override
    public void deleteEnquiry(Long id) {
        enquiryRepository.deleteById(id);
    }

    @Override
    public List<Enquiry> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return enquiryRepository.findByEnquiryDateBetween(startDate, endDate);
    }
}
