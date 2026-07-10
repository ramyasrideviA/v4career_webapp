package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.Payment;
import com.learn.learningarea.repository.PaymentRepository;
import com.learn.learningarea.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalDate;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Override
    public List<Payment> getLast32Days() {
        return repository.findLast32Days(LocalDate.now().minusDays(32));
    }

    @Autowired
    private PaymentRepository repository;

    @Override
    public Payment savePayment(Payment payment) {

        String category = payment.getCategory();

        if (category.equals("Registration Fee")) {
            payment.setInstallments(1);
            payment.setPaymentStatus("Remaining 2 Installments");
        } else if (category.equals("1st Installment")) {
            payment.setInstallments(2);
            payment.setPaymentStatus("Remaining 1 Installment");
        } else if (category.equals("2nd Installment")) {
            payment.setInstallments(3);
            payment.setPaymentStatus("Full Amount Paid");
        } else if (category.equals("Full Payment")) {
            payment.setInstallments(0);
            payment.setPaymentStatus("Full Amount Paid");
            payment.setNextInstallmentDate(null);
        }

        if (!"Full Amount Paid".equals(payment.getPaymentStatus())) {
            payment.setReminderSent7Days(false);
            payment.setReminderSent3Days(false);
        }
        return repository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    @Override
    public void deletePayment(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Payment> searchPayments(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPayments();
        }
        return repository.searchPayments(query);
    }

    @Override
    public List<Payment> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByCreatedAtBetween(startDate, endDate);
    }
}
