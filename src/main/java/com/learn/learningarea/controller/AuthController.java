package com.learn.learningarea.controller;

import com.learn.learningarea.model.User;
import com.learn.learningarea.repository.auth.UserRepository;
import com.learn.learningarea.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Temporary storage for OTPs (in production, use a cache like Redis)
    private final Map<String, String> otpStorage = new HashMap<>();

    @GetMapping("/signin")
    public String signin() {
        return "signin";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/send-otp")
    @ResponseBody
    public ResponseEntity<String> sendOtp(@RequestParam("email") String email,
        @RequestParam(value = "type", defaultValue = "signup") String type) {

    System.out.println("STEP 1");
    Optional<User> userOpt = userRepository.findByEmailId(email);

    System.out.println("STEP 2");

    String name = userOpt.isPresent() ? userOpt.get().getFirstName() : "User";

    String otp = emailService.generateOtp();

    System.out.println("Generated OTP = " + otp);

    otpStorage.put(email, otp);

    System.out.println("STEP 3");

    emailService.sendOtpEmail(email, otp, name);

    System.out.println("STEP 4");

    return ResponseEntity.ok("OTP sent");
}
    @PostMapping("/verify-otp")
    @ResponseBody
    public ResponseEntity<String> verifyOtp(@RequestParam("email") String email, @RequestParam("otp") String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            return ResponseEntity.ok("OTP verified");
        }
        return ResponseEntity.badRequest().body("Invalid OTP");
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("user") User user, @RequestParam("verified") boolean verified,
            Model model) {
        if (!verified) {
            model.addAttribute("error", "Please verify your email via OTP.");
            return "signup";
        }

        if (!user.getPassword().equals(user.getReEnterPassword())) {
            model.addAttribute("error", "Passwords do not match.");
            return "signup";
        }

        if (userRepository.findByEmailId(user.getEmailId()).isPresent()) {
            model.addAttribute("error", "Email already registered!");
            return "signup";
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            model.addAttribute("error", "Please select a role.");
            return "signup";
        }

        if (user.getBranch() == null || user.getBranch().isEmpty()) {
            model.addAttribute("error", "Please select a branch.");
            return "signup";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(true);
        userRepository.save(user);

        otpStorage.remove(user.getEmailId());
        return "redirect:/signin?success";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        return "forgot-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("emailId") String email,
            @RequestParam("password") String password,
            @RequestParam("reEnterPassword") String reEnterPassword,
            @RequestParam("verified") boolean verified,
            Model model) {
        if (!verified) {
            model.addAttribute("error", "Please verify your email via OTP.");
            return "forgot-password";
        }

        if (!password.equals(reEnterPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "forgot-password";
        }

        Optional<User> userOpt = userRepository.findByEmailId(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Email not registered!");
            return "forgot-password";
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        otpStorage.remove(email);
        return "redirect:/signin?success=Password reset successful";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/signin";
    }
}
