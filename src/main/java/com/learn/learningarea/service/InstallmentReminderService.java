package com.learn.learningarea.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.learn.learningarea.model.Payment;
import com.learn.learningarea.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InstallmentReminderService {
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    // @Scheduled(cron = "0 0 9 * * ?") // main line
    @Scheduled(cron = "0 * * * * ?") // for testing i use this line
    public void sendInstallmentReminders() {

        List<Payment> payments = paymentRepository.findAllWithNextInstallmentDate();
        LocalDate today = LocalDate.now();
        for (Payment payment : payments) {
            if ("Full Amount Paid".equalsIgnoreCase(payment.getPaymentStatus())) {
                continue;
            }
            String dueDateStr = payment.getNextInstallmentDate();
            if (dueDateStr == null || dueDateStr.trim().isEmpty())
                continue;
            LocalDate dueDate;
            try {
                dueDate = LocalDate.parse(dueDateStr);
            } catch (Exception e) {
                continue;
            }
            System.out.println("Reminder scheduler running...");
            long daysLeft = ChronoUnit.DAYS.between(today, dueDate);
            if (daysLeft == 7 && Boolean.FALSE.equals(payment.getReminderSent7Days())) {
                emailService.sendEmail(
                        payment.getEmailId(),
                        "Installment Reminder",
                        "Dear " + payment.getName()
                                + ", your installment is due on "
                                + dueDate
                                + ". Please complete payment.");
                payment.setReminderSent7Days(true);
                paymentRepository.save(payment);
                System.out.println("Reminder email triggered for: " + payment.getEmailId());
            }
            if (daysLeft == 3 && Boolean.FALSE.equals(payment.getReminderSent3Days())) {
                emailService.sendEmail(
                        payment.getEmailId(),
                        "Installment Reminder",
                        "Dear " + payment.getName()
                                + ", your installment is due on "
                                + dueDate
                                + ". Please complete payment.");
                payment.setReminderSent3Days(true);
                paymentRepository.save(payment);
                System.out.println("Reminder email triggered for: " + payment.getEmailId());
            }
        }
    }
}
