package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.Vendor;
import com.learn.learningarea.repository.VendorRepository;
import com.learn.learningarea.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalDate;

@Service
public class VendorServiceImpl implements VendorService {
    @Override
    public List<Vendor> getLast32Days() {
        return repository.findLast32Days(LocalDate.now().minusDays(32));
    }
    @Autowired private VendorRepository repository;
    @Override public Vendor saveVendor(Vendor vendor) { return repository.save(vendor); }
    @Override public List<Vendor> getAllVendors() { return repository.findAll(); }
    @Override
    public List<Vendor> searchVendors(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllVendors();
        }
        return repository.searchVendors(query);
    }
    @Override public void deleteVendor(Long id) { repository.deleteById(id); }

    @Override
    public List<Vendor> getReportsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return repository.findByCreatedAtBetween(startDate, endDate);
    }
}
