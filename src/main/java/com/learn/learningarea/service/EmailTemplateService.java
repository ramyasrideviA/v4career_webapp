package com.learn.learningarea.service;

import com.learn.learningarea.model.EmailTemplate;
import java.util.List;
import java.util.Optional;

public interface EmailTemplateService {
    EmailTemplate saveTemplate(EmailTemplate template);

    List<EmailTemplate> getAllTemplates();

    Optional<EmailTemplate> getTemplateById(Long id);

    void deleteTemplate(Long id);

    List<EmailTemplate> searchTemplates(String name);
}
