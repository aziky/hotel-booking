package com.nls.notificationservice.domain.repository;

import com.nls.notificationservice.domain.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ITemplateRepository extends JpaRepository<Template, UUID> {

    Template findByType(String type);

}
