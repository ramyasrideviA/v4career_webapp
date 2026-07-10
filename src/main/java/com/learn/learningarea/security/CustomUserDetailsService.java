package com.learn.learningarea.security;

import com.learn.learningarea.model.User;
import com.learn.learningarea.repository.auth.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

        @Autowired
        private UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {

                User user = userRepository.findByEmailId(emailId)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + emailId));

                String role = user.getRole() != null ? user.getRole() : "STUDENT";

                Collection<SimpleGrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

                return new CustomUserDetails(
                                user.getEmailId(),
                                user.getPassword(),
                                user.getBranch(), // now String → correct
                                authorities);
        }

}