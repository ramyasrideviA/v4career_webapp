package com.learn.learningarea.service;

import com.learn.learningarea.model.Payment;
import java.util.List;

public interface PaymentService {
    Payment savePayment(Payment payment);

    List<Payment> getAllPayments();

    void deletePayment(Long id);

    List<Payment> searchPayments(String query);
    List<Payment> getLast32Days();

    List<Payment> getReportsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
