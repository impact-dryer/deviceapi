package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractPostgresContainerTest {

  protected static final PostgreSQLContainer<?> POSTGRES =
          new PostgreSQLContainer<>("postgres:18-alpine")
                  .withDatabaseName("deviceapi")
                  .withUsername("test")
                  .withPassword("test");

  @BeforeAll
  static void startContainer() {
    if (!POSTGRES.isRunning()) {
      POSTGRES.start();
    }
  }

  @AfterAll
  static void stopContainer() {
    if (POSTGRES.isRunning()) {
      POSTGRES.stop();
    }
  }

  @DynamicPropertySource
  static void registerDataSource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
  }
}
