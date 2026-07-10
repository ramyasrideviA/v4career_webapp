package com.learn.learningarea.controller;

import com.learn.learningarea.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping({"/admin/export", "/employee/export"})
public class ReportExportController {

    @Autowired private ReportExportService reportExportService;
    @Autowired private EmailService emailService;
    @Autowired private EnquiryService enquiryService;
    @Autowired private com.learn.learningarea.service.EnrollmentService enrollmentService;
    @Autowired private IncomeService incomeService;
    @Autowired private ExpenseService expenseService;
    @Autowired private PaymentService paymentService;
    @Autowired private ScheduleService scheduleService;
    @Autowired private DailyReportService dailyReportService;
    @Autowired private CollegeService collegeService;
    @Autowired private CorporateService corporateService;
    @Autowired private EmployeeService employeeService;
    @Autowired private VendorService vendorService;

    private List<?> fetchData(String accountType, LocalDate startDate, LocalDate endDate) {
        if (accountType == null || startDate == null || endDate == null) return Collections.emptyList();
        switch (accountType) {
            case "enquiry": return enquiryService.getReportsByDateRange(startDate, endDate);
            case "enrollment": return enrollmentService.getReportsByDateRange(startDate, endDate);
            case "income": return incomeService.getReportsByDateRange(startDate, endDate);
            case "expense": return expenseService.getReportsByDateRange(startDate, endDate);
            case "payment": return paymentService.getReportsByDateRange(startDate, endDate);
            case "schedule": return scheduleService.getReportsByDateRange(startDate, endDate);
            case "daily_report": return dailyReportService.getReportsByDateRange(startDate, endDate);
            case "college": return collegeService.getReportsByDateRange(startDate, endDate);
            case "corporate": return corporateService.getReportsByDateRange(startDate, endDate);
            case "employee": return employeeService.getReportsByDateRange(startDate, endDate);
            case "vendor": return vendorService.getReportsByDateRange(startDate, endDate);
            default: return Collections.emptyList();
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam("accountType") String accountType,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            List<?> data = fetchData(accountType, startDate, endDate);
            
            byte[] pdfBytes = reportExportService.generatePdfReport(data, accountType);
            String filename = "report_" + accountType + "_" + startDateStr + "_to_" + endDateStr + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam("accountType") String accountType,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            List<?> data = fetchData(accountType, startDate, endDate);
            
            byte[] excelBytes = reportExportService.generateExcelReport(data, accountType);
            String filename = "report_" + accountType + "_" + startDateStr + "_to_" + endDateStr + ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/email")
    public String emailReport(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestParam("accountType") String accountType,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            @RequestParam("emailTo") String emailTo) {
        
        String redirectBase = request.getRequestURI().startsWith("/employee") ? "/employee/home" : "/admin/home";
        
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            List<?> data = fetchData(accountType, startDate, endDate);
            
            byte[] pdfBytes = reportExportService.generatePdfReport(data, accountType);
            String filename = "report_" + accountType + ".pdf";
            
            String subject = "Manage Accounts Report: " + accountType.toUpperCase();
            String body = "<p>Please find attached the requested report for <strong>" + accountType.toUpperCase() + "</strong> " +
                          "from " + startDateStr + " to " + endDateStr + ".</p>";
            
            emailService.sendEmailWithAttachment(emailTo, subject, body, pdfBytes, filename);
            
            return "redirect:" + redirectBase + "?content=manage-accounts&success=true&accountType=" + accountType + 
                   "&startDate=" + startDateStr + "&endDate=" + endDateStr;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:" + redirectBase + "?content=manage-accounts&error=true&accountType=" + accountType + 
                   "&startDate=" + startDateStr + "&endDate=" + endDateStr;
        }
    }
}
