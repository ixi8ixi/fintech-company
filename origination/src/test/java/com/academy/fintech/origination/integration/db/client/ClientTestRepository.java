package com.academy.fintech.origination.integration.db.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientTestRepository extends JpaRepository<ClientTestEntity, String> {
    List<ClientTestEntity> findAllByEmail(String email);
}
