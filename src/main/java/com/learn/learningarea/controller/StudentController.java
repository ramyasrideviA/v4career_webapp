package com.learn.learningarea.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.learn.learningarea.repository.auth.UserRepository;

import java.security.Principal;

@Controller
public class StudentController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/student/home")
    public String studentHome(Principal principal, Model model) {
        userRepository.findByEmailId(principal.getName()).ifPresent(user -> {
            model.addAttribute("firstName", user.getFirstName());
        });
        return "student/home";
    }
}
