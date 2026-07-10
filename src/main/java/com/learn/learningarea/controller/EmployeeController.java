package com.learn.learningarea.controller;

import com.learn.learningarea.dto.TimeSlot;
import com.learn.learningarea.model.*;
import com.learn.learningarea.repository.*;
import com.learn.learningarea.repository.auth.UserRepository;
import com.learn.learningarea.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.*;
import java.io.InputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FranchiseRepository franchiseRepository;
    @Autowired
    private IncomeService incomeService;
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private EnquiryService enquiryService;
    @Autowired
    private EmailTemplateService emailTemplateService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private com.learn.learningarea.service.EnrollmentService enrollmentService;

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private SlotService slotService;

    @ModelAttribute
    public void commonAttributes(Principal principal, Model model) {
        if (principal != null) {
            userRepository.findByEmailId(principal.getName()).ifPresent(user -> {
                model.addAttribute("firstName", user.getFirstName());
            });
        }
    }

    private String formatSearchQueryForDB(String search) {
        if (search != null && search.matches("^\\d{2}-\\d{2}-\\d{4}$")) {
            String[] parts = search.split("-");
            return parts[2] + "-" + parts[1] + "-" + parts[0];
        }
        return search;
    }

    @GetMapping("/home")
    public String employeeHome(
            @RequestParam(name = "content", required = false, defaultValue = "dashboard") String content,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "accountType", required = false) String accountType,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            Principal principal,
            Model model) {

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        // Calculate Enquiry Statistics
        List<Enquiry> allEnquiries = enquiryService.getAllEnquiries();
        long totalEnquiries = allEnquiries.size();
        long monthlyEnquiries = allEnquiries.stream()
                .filter(e -> e.getEnquiryDate() != null &&
                        e.getEnquiryDate().getMonthValue() == currentMonth &&
                        e.getEnquiryDate().getYear() == currentYear)
                .count();

        // Calculate Enrollment Statistics
        List<Enrollment> allEnrollments = enrollmentService.getAllEnrollments();
        long totalEnrollments = allEnrollments.size();
        long monthlyEnrollments = allEnrollments.stream()
                .filter(e -> e.getEnrollmentDate() != null &&
                        e.getEnrollmentDate().getMonthValue() == currentMonth &&
                        e.getEnrollmentDate().getYear() == currentYear)
                .count();

        // Calculate Revenue (Income) Statistics
        List<Income> allIncomes = incomeService.getAllIncomes();
        double totalRevenue = allIncomes.stream().mapToDouble(Income::getAmount).sum();
        double monthlyRevenue = allIncomes.stream()
                .filter(i -> i.getIncomeDate() != null &&
                        i.getIncomeDate().getMonthValue() == currentMonth &&
                        i.getIncomeDate().getYear() == currentYear)
                .mapToDouble(Income::getAmount).sum();

        // Calculate Expense Statistics (to find Profit)
        List<Expense> allExpenses = expenseService.getAllExpenses();
        double totalExpenses = allExpenses.stream().mapToDouble(Expense::getAmount).sum();
        double monthlyExpenses = allExpenses.stream()
                .filter(e -> e.getExpenseDate() != null &&
                        e.getExpenseDate().getMonthValue() == currentMonth &&
                        e.getExpenseDate().getYear() == currentYear)
                .mapToDouble(Expense::getAmount).sum();

        // Calculate Profit
        double totalProfit = totalRevenue - totalExpenses;
        double monthlyProfit = monthlyRevenue - monthlyExpenses;

        model.addAttribute("totalEnquiries", totalEnquiries);
        model.addAttribute("monthlyEnquiries", monthlyEnquiries);
        model.addAttribute("totalEnrollments", totalEnrollments);
        model.addAttribute("monthlyEnrollments", monthlyEnrollments);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("totalProfit", totalProfit);
        model.addAttribute("monthlyProfit", monthlyProfit);

        String dbSearchQuery = formatSearchQueryForDB(search);
        // Content Handlers
        if ("all-enquiries".equals(content)) {
            List<Enquiry> enquiries;
            if (search != null && !search.trim().isEmpty()) {
                enquiries = enquiryService.searchEnquiries(dbSearchQuery);
                model.addAttribute("searchQuery", search);
            } else {
                enquiries = enquiryService.getLast32Days();
            }
            model.addAttribute("enquiries", enquiries);
        } else if ("all-enrollments".equals(content)) {
            List<Enrollment> enrollments;
            if (search != null && !search.trim().isEmpty()) {
                enrollments = enrollmentService.searchEnrollments(dbSearchQuery);
                model.addAttribute("searchQuery", search);
            } else {
                enrollments = enrollmentService.getLast32Days();
            }
            model.addAttribute("enrollments", enrollments);
        } else if ("add-enrollment".equals(content)) {
            model.addAttribute("nextEnrollmentId", enrollmentService.generateNextEnrollmentId());
        } else if ("send-mail".equals(content)) {
            model.addAttribute("templates", emailTemplateService.getAllTemplates());
        } else if ("templates".equals(content)) {
            List<EmailTemplate> templates;
            if (search != null && !search.trim().isEmpty()) {
                templates = emailTemplateService.searchTemplates(dbSearchQuery);
                model.addAttribute("searchQuery", search);
            } else {
                templates = emailTemplateService.getAllTemplates();
            }
            model.addAttribute("templates", templates);
        } else if ("all-colleges".equals(content)) {
            List<College> colleges;
            if (search != null && !search.trim().isEmpty()) {
                colleges = collegeService.searchColleges(dbSearchQuery);
                model.addAttribute("searchQuery", search);
            } else {
                colleges = collegeService.getLast32Days();
            }
            model.addAttribute("colleges", colleges);
        } else if ("all-corporates".equals(content)) {
            List<Corporate> corporates;
            if (search != null && !search.trim().isEmpty()) {
                corporates = corporateService.searchCorporates(dbSearchQuery);
                model.addAttribute("searchQuery", search);
            } else {
                corporates = corporateService.getLast32Days();
            }
            model.addAttribute("corporates", corporates);
        } else if ("all-vendors".equals(content)) {
            List<Vendor> vendors;
            if (search != null && !search.trim().isEmpty()) {
                vendors = vendorService.searchVendors(dbSearchQuery);
                model.addAttribute("searchQuery", search);
            } else {
                vendors = vendorService.getLast32Days();
            }
            model.addAttribute("vendors", vendors);
        } else if ("all-employees".equals(content)) {
            List<Employee> employees;
            if (search != null && !search.trim().isEmpty()) {
                employees = employeeService.searchEmployees(dbSearchQuery);
                model.addAttribute("searchQuery", search);
            } else {
                employees = employeeService.getLast32Days();
            }
            model.addAttribute("employees", employees);
        } else if ("all-schedules".equals(content)) {
            List<Schedule> schedules;
            if (search != null && !search.trim().isEmpty()) {
                schedules = scheduleService.searchSchedules(dbSearchQuery);
                model.addAttribute("searchQuery", search);
            } else {
                schedules = scheduleService.getLast32Days();
            }
            model.addAttribute("schedules", schedules);
        } else if ("profile".equals(content)) {
            if (principal != null) {
                userRepository.findByEmailId(principal.getName()).ifPresent(user -> {
                    Franchise franchise = null;
                    if (user.getFranchiseId() != null) {
                        franchise = franchiseRepository.findById(user.getFranchiseId()).orElse(null);
                    }
                    if (franchise != null) {
                        model.addAttribute("franchise", franchise);
                        model.addAttribute("franchiseName", franchise.getFranchiseName());
                        model.addAttribute("emailId", franchise.getEmailId());
                        model.addAttribute("mobileNumber", franchise.getMobileNumber());
                        model.addAttribute("address", franchise.getAddress());
                        model.addAttribute("country", franchise.getCountry());
                        model.addAttribute("instituteLogo", franchise.getLogoPath());
                    }
                });
            }
        } else if ("manage-accounts".equals(content)) {
            if (accountType != null && startDateStr != null && endDateStr != null
                    && !accountType.isEmpty() && !startDateStr.isEmpty() && !endDateStr.isEmpty()) {
                try {
                    LocalDate startDate = LocalDate.parse(startDateStr);
                    LocalDate endDate = LocalDate.parse(endDateStr);

                    switch (accountType) {
                        case "enquiry":
                            model.addAttribute("reportData", enquiryService.getReportsByDateRange(startDate, endDate));
                            break;
                        case "enrollment":
                            model.addAttribute("reportData",
                                    enrollmentService.getReportsByDateRange(startDate, endDate));
                            break;
                        case "income":
                            model.addAttribute("reportData", incomeService.getReportsByDateRange(startDate, endDate));
                            break;
                        case "expense":
                            model.addAttribute("reportData", expenseService.getReportsByDateRange(startDate, endDate));
                            break;
                        case "schedule":
                            model.addAttribute("reportData", scheduleService.getReportsByDateRange(startDate, endDate));
                            break;
                        case "college":
                            model.addAttribute("reportData", collegeService.getReportsByDateRange(startDate, endDate));
                            break;
                        case "corporate":
                            model.addAttribute("reportData",
                                    corporateService.getReportsByDateRange(startDate, endDate));
                            break;
                        case "employee":
                            model.addAttribute("reportData", employeeService.getReportsByDateRange(startDate, endDate));
                            break;
                        case "vendor":
                            model.addAttribute("reportData", vendorService.getReportsByDateRange(startDate, endDate));
                            break;
                    }
                    model.addAttribute("selectedAccountType", accountType);
                    model.addAttribute("selectedStartDate", startDateStr);
                    model.addAttribute("selectedEndDate", endDateStr);
                } catch (Exception e) {
                    model.addAttribute("errorMessage", "Invalid date format");
                }
            }
        }

        model.addAttribute("content", content);
        return "employee/home";
    }

    @PostMapping("/profile/update")
    @Transactional
    public String updateProfile(
            @RequestParam("franchiseName") String franchiseName,
            @RequestParam("emailId") String emailId,
            @RequestParam("mobileNumber") String mobileNumber,
            @RequestParam("address") String address,
            @RequestParam("country") String country,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            Principal principal) {

        if (principal == null) {
            return "redirect:/employee/home?content=profile&error=nouser";
        }

        Optional<User> userOpt = userRepository.findByEmailId(principal.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/employee/home?content=profile&error=usernotfound";
        }

        User user = userOpt.get();
        Franchise franchise = null;
        if (user.getFranchiseId() != null) {
            franchise = franchiseRepository.findById(user.getFranchiseId()).orElse(null);
        }

        if (franchise == null) {
            franchise = new Franchise();
        }

        franchise.setFranchiseName(franchiseName);
        franchise.setEmailId(emailId);
        franchise.setMobileNumber(mobileNumber);
        franchise.setAddress(address);
        franchise.setCountry(country);

        if (logo != null && !logo.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + logo.getOriginalFilename();
                Path uploadPath = Paths.get("uploads/logo/");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = logo.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    franchise.setLogoPath("/logo/" + fileName);
                }
            } catch (Exception e) {
                System.err.println("Logo upload failed: " + e.getMessage());
            }
        }

        franchise = franchiseRepository.save(franchise);
        user.setFranchiseId(franchise.getId());
        userRepository.save(user);

        return "redirect:/employee/home?content=profile&updated=true";
    }

    @PostMapping("/enquiries/add")
    public String addEnquiry(@ModelAttribute Enquiry enquiry) {
        enquiryService.saveEnquiry(enquiry);
        return "redirect:/employee/home?content=all-enquiries";
    }

    @PostMapping("/enquiries/update")
    public String updateEnquiry(@ModelAttribute Enquiry enquiry) {
        enquiryService.saveEnquiry(enquiry);
        return "redirect:/employee/home?content=all-enquiries";
    }

    @GetMapping("/enquiries/delete/{id}")
    public String deleteEnquiry(@PathVariable("id") Long id) {
        enquiryService.deleteEnquiry(id);
        return "redirect:/employee/home?content=all-enquiries";
    }

    @PostMapping("/enrollments/add")
    public String addEnrollment(@ModelAttribute com.learn.learningarea.model.Enrollment enrollment) {
        String nextId = enrollmentService.generateNextEnrollmentId();
        enrollment.setEnrollmentId(nextId);
        enrollmentService.saveEnrollment(enrollment);
        return "redirect:/employee/home?content=all-enrollments";
    }

    @PostMapping("/enrollments/update")
    public String updateEnrollment(@ModelAttribute com.learn.learningarea.model.Enrollment enrollment) {
        enrollmentService.saveEnrollment(enrollment);
        return "redirect:/employee/home?content=all-enrollments";
    }

    @GetMapping("/enrollments/delete/{id}")
    public String deleteEnrollment(@PathVariable("id") Long id) {
        enrollmentService.deleteEnrollment(id);
        return "redirect:/employee/home?content=all-enrollments";
    }

    @PostMapping("/email/send")
    public String sendEmail(@RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("body") String body) {
        emailService.sendEmail(to, subject, body);
        return "redirect:/employee/home?content=send-mail&sent=true";
    }

    @PostMapping("/email/templates/add")
    public String addTemplate(@ModelAttribute EmailTemplate template) {
        if (template.getSubject() == null || template.getSubject().isEmpty()) {
            template.setSubject(template.getName());
        }
        emailTemplateService.saveTemplate(template);
        return "redirect:/employee/home?content=templates&saved=true";
    }

    @PostMapping("/email/templates/update")
    public String updateTemplate(@ModelAttribute EmailTemplate template) {
        if (template.getSubject() == null || template.getSubject().isEmpty()) {
            template.setSubject(template.getName());
        }
        emailTemplateService.saveTemplate(template);
        return "redirect:/employee/home?content=templates&updated=true";
    }

    @GetMapping("/email/templates/delete/{id}")
    public String deleteTemplate(@PathVariable("id") Long id) {
        emailTemplateService.deleteTemplate(id);
        return "redirect:/employee/home?content=templates";
    }

    // College Handlers
    @PostMapping("/colleges/add")
    public String addCollege(@ModelAttribute College college) {
        collegeService.saveCollege(college);
        return "redirect:/employee/home?content=all-colleges";
    }

    @PostMapping("/colleges/update")
    public String updateCollege(@ModelAttribute College college) {
        collegeService.saveCollege(college);
        return "redirect:/employee/home?content=all-colleges";
    }

    @GetMapping("/colleges/delete/{id}")
    public String deleteCollege(@PathVariable Long id) {
        collegeService.deleteCollege(id);
        return "redirect:/employee/home?content=all-colleges";
    }

    // Corporate Handlers
    @PostMapping("/corporates/add")
    public String addCorporate(@ModelAttribute Corporate corporate) {
        corporateService.saveCorporate(corporate);
        return "redirect:/employee/home?content=all-corporates";
    }

    @PostMapping("/corporates/update")
    public String updateCorporate(@ModelAttribute Corporate corporate) {
        corporateService.saveCorporate(corporate);
        return "redirect:/employee/home?content=all-corporates";
    }

    @GetMapping("/corporates/delete/{id}")
    public String deleteCorporate(@PathVariable Long id) {
        corporateService.deleteCorporate(id);
        return "redirect:/employee/home?content=all-corporates";
    }

    // Vendor Handlers
    @PostMapping("/vendors/add")
    public String addVendor(@ModelAttribute Vendor vendor) {
        vendorService.saveVendor(vendor);
        return "redirect:/employee/home?content=all-vendors";
    }

    @PostMapping("/vendors/update")
    public String updateVendor(@ModelAttribute Vendor vendor) {
        vendorService.saveVendor(vendor);
        return "redirect:/employee/home?content=all-vendors";
    }

    @GetMapping("/vendors/delete/{id}")
    public String deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
        return "redirect:/employee/home?content=all-vendors";
    }

    // Employee Handlers
    @PostMapping("/employees/add")
    public String addEmployee(@ModelAttribute Employee employee) {
        employeeService.saveEmployee(employee);
        return "redirect:/employee/home?content=all-employees";
    }

    @PostMapping("/employees/update")
    public String updateEmployee(@ModelAttribute Employee employee) {
        employeeService.saveEmployee(employee);
        return "redirect:/employee/home?content=all-employees";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employee/home?content=all-employees";
    }

    // Schedule Handlers
    @PostMapping("/schedules/add")
    public String addSchedule(@ModelAttribute Schedule schedule) {
        scheduleService.saveSchedule(schedule);
        return "redirect:/employee/home?content=all-schedules";
    }

    @PostMapping("/schedules/update")
    public String updateSchedule(@ModelAttribute Schedule schedule) {
        scheduleService.saveSchedule(schedule);
        return "redirect:/employee/home?content=all-schedules";
    }

    @GetMapping("/schedules/delete/{id}")
    public String deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return "redirect:/employee/home?content=all-schedules";
    }

    @GetMapping("/schedules/api/all")
    @ResponseBody
    public List<Schedule> getAllSchedulesApi() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/schedules/api/generate-batch-no")
    @ResponseBody
    public String generateBatchNo(@RequestParam String batchType) {
        return scheduleService.generateNextBatchNo(batchType);
    }

    @GetMapping("/schedules/api/day-slots")
    @ResponseBody
    public Map<String, Object> getSlotsForDate(@RequestParam String date) {
        LocalDate selectedDate = LocalDate.parse(date);
        List<Schedule> booked = slotService.getBookingsForDate(selectedDate);
        List<TimeSlot> available = slotService.getAvailableSlots(selectedDate);
        Map<String, Object> map = new HashMap<>();
        map.put("booked", booked);
        map.put("available", available);
        return map;
    }

    @GetMapping("/api/service-income")
    @ResponseBody
    public Map<String, Double> getServiceIncome(@RequestParam(defaultValue = "monthly") String filter) {
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = now;

        if ("yearly".equals(filter)) {
            startDate = LocalDate.of(now.getYear(), 1, 1);
        } else {
            startDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1);
        }

        List<Enrollment> enrollments = enrollmentService.getReportsByDateRange(startDate, endDate);
        Map<String, Double> result = new java.util.LinkedHashMap<>();
        for (Enrollment e : enrollments) {
            String service = e.getService() != null ? e.getService() : "Unknown";
            double amount = 0;
            try {
                if (e.getAmount() != null && !e.getAmount().trim().isEmpty()) {
                    amount = Double.parseDouble(e.getAmount().replaceAll("[^0-9.]", ""));
                }
            } catch (NumberFormatException ex) {
                /* skip */ }
            result.merge(service, amount, (a, b) -> a + b);
        }
        return result;
    }

    @GetMapping("/api/fee-collection")
    @ResponseBody
    public Map<String, Object> getFeeCollection() {
        List<Payment> allPayments = paymentService.getAllPayments();

        double totalCollected = 0;
        for (Payment p : allPayments) {
            try {
                if (p.getAmount() != null && !p.getAmount().trim().isEmpty()) {
                    totalCollected += Double.parseDouble(p.getAmount().replaceAll("[^0-9.]", ""));
                }
            } catch (NumberFormatException ex) {
                /* skip */ }
        }

        Set<String> completedIds = new HashSet<>();
        for (Payment p : allPayments) {
            if ("2nd Installment".equalsIgnoreCase(p.getCategory())
                    || "Full Payment".equalsIgnoreCase(p.getCategory())) {
                if (p.getEnrollmentId() != null)
                    completedIds.add(p.getEnrollmentId());
            }
        }

        Map<String, String> pendingMap = new LinkedHashMap<>();
        for (Payment p : allPayments) {
            String eid = p.getEnrollmentId();
            if (eid == null || completedIds.contains(eid))
                continue;
            pendingMap.put(eid, p.getCategory());
        }

        Set<String> regOnlyIds = new HashSet<>();
        Set<String> inst1Ids = new HashSet<>();
        for (Payment p : allPayments) {
            String eid = p.getEnrollmentId();
            if (eid == null || completedIds.contains(eid))
                continue;
            if ("Registration Fee".equalsIgnoreCase(p.getCategory()))
                regOnlyIds.add(eid);
            if ("1st Installment".equalsIgnoreCase(p.getCategory()))
                inst1Ids.add(eid);
        }
        Set<String> pendingFirst = new HashSet<>(regOnlyIds);
        pendingFirst.removeAll(inst1Ids);
        Set<String> pendingSecond = new HashSet<>(inst1Ids);

        Map<String, Object> result2 = new LinkedHashMap<>();
        result2.put("totalCollected", totalCollected);
        result2.put("pendingFirst", pendingFirst.size()); // awaiting 1st installment
        result2.put("pendingSecond", pendingSecond.size()); // awaiting 2nd installment
        result2.put("totalPending", pendingFirst.size() + pendingSecond.size());
        return result2;
    }
}
