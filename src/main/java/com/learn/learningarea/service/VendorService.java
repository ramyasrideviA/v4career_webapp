package com.learn.learningarea.service;

import com.learn.learningarea.model.Vendor;
import java.util.List;
import java.time.LocalDate;

public interface VendorService {
    Vendor saveVendor(Vendor vendor);

    List<Vendor> getAllVendors();

    List<Vendor> searchVendors(String query);

    List<Vendor> getLast32Days();

    void deleteVendor(Long id);

    List<Vendor> getReportsByDateRange(LocalDate startDate, LocalDate endDate);
}
