package com.local.rag.config;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.client.RestTemplate;

class AppConfigTest {

  private final AppConfig appConfig = new AppConfig();

  @Test
  void dataSource_usesJdbcUrlFromEnvironment() {
    MockEnvironment environment = new MockEnvironment();
    environment.setProperty("spring.datasource.url", "jdbc:sqlite::memory:");

    DataSource dataSource = appConfig.dataSource(environment);

    assertThat(dataSource).isNotNull();
  }

  @Test
  void restTemplateAndJdbcTemplate_areCreated() {
    OllamaProperties properties = new OllamaProperties();
    properties.setConnectTimeoutMs(500);
    properties.setReadTimeoutMs(1000);

    RestTemplate restTemplate = appConfig.restTemplate(new RestTemplateBuilder(), properties);
    DataSource dataSource = appConfig.dataSource(new MockEnvironment());

    assertThat(restTemplate).isNotNull();
    assertThat(appConfig.jdbcTemplate(dataSource)).isInstanceOf(JdbcTemplate.class);
  }
}
