package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.EmailTemplate;
import com.learn.learningarea.repository.EmailTemplateRepository;
import com.learn.learningarea.service.EmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Override
    public EmailTemplate saveTemplate(EmailTemplate template) {
        return emailTemplateRepository.save(template);
    }

    @Override
    public List<EmailTemplate> getAllTemplates() {
        return emailTemplateRepository.findAll();
    }

    @Override
    public Optional<EmailTemplate> getTemplateById(Long id) {
        return emailTemplateRepository.findById(id);
    }

    @Override
    public void deleteTemplate(Long id) {
        emailTemplateRepository.deleteById(id);
    }

    @Override
    public List<EmailTemplate> searchTemplates(String name) {
        if (name == null || name.trim().isEmpty()) {
            return emailTemplateRepository.findAll();
        }
        return emailTemplateRepository.searchTemplates(name);
    }
}
