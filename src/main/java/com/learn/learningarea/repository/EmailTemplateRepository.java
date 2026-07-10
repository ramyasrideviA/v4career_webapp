package com.learn.learningarea.repository;

import com.learn.learningarea.model.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    @Query("SELECT et FROM EmailTemplate et WHERE " +
            "LOWER(et.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<EmailTemplate> searchTemplates(@Param("query") String query);
}
