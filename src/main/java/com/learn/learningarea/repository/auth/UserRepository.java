package com.learn.learningarea.repository.auth;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.learn.learningarea.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@EnableJpaRepositories(basePackages = "com.learn.learningarea.repository.auth")
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailId(String emailId);
}
