package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.Franchise;
import com.learn.learningarea.model.User;
import com.learn.learningarea.repository.FranchiseRepository;
import com.learn.learningarea.repository.auth.UserRepository;
import com.learn.learningarea.service.FranchiseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FranchiseServiceImpl implements FranchiseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FranchiseRepository franchiseRepository;

    @Override
    public Franchise getFranchiseByUserEmail(String email) {
        // STEP 1: find user
        User user = userRepository
                .findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // STEP 2: get franchiseId from user
        Long franchiseId = user.getFranchiseId();
        if (franchiseId == null) {
            throw new RuntimeException("User is not linked to any franchise");
        }

        // STEP 3: load franchise
        Franchise franchise = franchiseRepository
                .findById(franchiseId)
                .orElseThrow(() -> new RuntimeException("Franchise not found"));

        return franchise;
    }
}
