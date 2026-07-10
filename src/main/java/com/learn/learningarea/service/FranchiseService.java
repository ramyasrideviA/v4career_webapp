package com.learn.learningarea.service;

import com.learn.learningarea.model.Franchise;

public interface FranchiseService {
    Franchise getFranchiseByUserEmail(String email);
}
