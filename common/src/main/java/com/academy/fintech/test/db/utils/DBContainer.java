package com.academy.fintech.test.db.utils;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Test container with a PostgresSQL database. Database: fintech, username: postgres, password: postgres.
 */
public class DBContainer extends PostgreSQLContainer<DBContainer> {
    private static final String DB_IMAGE = "postgres:14.1-alpine";
    private static final String DB_NAME = "fintech";
    private static final String LOGIN = "postgres";
    private static final String PASSWORD = "postgres";

    public static DBContainer container = new DBContainer()
            .withDatabaseName(DB_NAME)
            .withUsername(LOGIN)
            .withPassword(PASSWORD);

    public DBContainer() {
        super(DB_IMAGE);
    }
}
