package com.academy.fintech.origination.integration.db.application;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationTestRepository extends JpaRepository<ApplicationTestEntity, String> {
}
