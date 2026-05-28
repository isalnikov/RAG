package com.local.rag.config;

import java.time.Duration;
import javax.sql.DataSource;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

/**
 * Конфигурация инфраструктурных бинов: DataSource, RestTemplate, JdbcTemplate.
 */
@Configuration
public class AppConfig {

    /**
     * Создаёт SQLite {@link DataSource} с включённой загрузкой расширений и внешними ключами.
     *
     * @param environment окружение Spring для чтения {@code spring.datasource.url}
     * @return настроенный источник данных
     */
    @Bean
    public DataSource dataSource(Environment environment) {
        String jdbcUrl = environment.getProperty("spring.datasource.url", "jdbc:sqlite:rag.db");
        SQLiteConfig config = new SQLiteConfig();
        config.enableLoadExtension(true);
        config.enforceForeignKeys(true);
        SQLiteDataSource dataSource = new SQLiteDataSource(config);
        dataSource.setUrl(jdbcUrl);
        return dataSource;
    }

    /**
     * Создаёт {@link RestTemplate} с таймаутами из {@link OllamaProperties}.
     *
     * @param builder          фабрика RestTemplate Spring Boot
     * @param ollamaProperties таймауты подключения и чтения
     * @return HTTP-клиент для Ollama
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, OllamaProperties ollamaProperties) {
        return builder
                .setConnectTimeout(Duration.ofMillis(ollamaProperties.getConnectTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(ollamaProperties.getReadTimeoutMs()))
                .build();
    }

    /**
     * Предоставляет {@link JdbcTemplate}, привязанный к SQLite.
     *
     * @param dataSource источник данных приложения
     * @return шаблон JDBC
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
