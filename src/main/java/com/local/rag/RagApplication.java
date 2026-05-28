package com.local.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Точка входа локальной RAG-системы.
 * <p>
 * Запускает Spring Boot-приложение с автоконфигурацией JDBC, REST API,
 * сканированием {@code @ConfigurationProperties} и инициализацией SQLite/sqlite-vec.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class RagApplication {

    /**
     * Запускает приложение.
     *
     * @param args аргументы командной строки (передаются в Spring Boot)
     */
    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }
}
