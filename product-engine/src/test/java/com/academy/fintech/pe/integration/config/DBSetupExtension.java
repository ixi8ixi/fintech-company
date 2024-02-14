package com.academy.fintech.pe.integration.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DBSetupExtension implements BeforeAllCallback {
    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        DBContainer.container.start();
        updateProperty(DBContainer.container);
    }

    private void updateProperty(DBContainer container) {
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.password", container.getPassword());
    }
}
