package com.local.rag.service;

import com.local.rag.config.SqliteProperties;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * При старте приложения загружает sqlite-vec (если указан путь) и выполняет {@code init_vec.sql}.
 */
@Component
public class DatabaseInitializer {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final SqliteProperties sqliteProperties;

    /**
     * @param dataSource       источник данных SQLite
     * @param jdbcTemplate     шаблон для DDL из скрипта
     * @param sqliteProperties путь к расширению vec0
     */
    public DatabaseInitializer(DataSource dataSource, JdbcTemplate jdbcTemplate, SqliteProperties sqliteProperties) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.sqliteProperties = sqliteProperties;
    }

    /**
     * Вызывается Spring после создания бина: расширение + схема таблиц.
     *
     * @throws IllegalStateException при ошибке загрузки расширения или чтения SQL
     */
    @PostConstruct
    public void initialize() {
        loadVecExtensionIfConfigured();
        executeInitScript();
    }

    private void loadVecExtensionIfConfigured() {
        String path = sqliteProperties.getVecExtensionPath();
        if (path == null || path.isBlank()) {
            return;
        }
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String escaped = path.replace("'", "''");
            statement.execute("SELECT load_extension('" + escaped + "')");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load sqlite-vec extension from: " + path, e);
        }
    }

    private void executeInitScript() {
        try {
            String sql = new String(
                    new ClassPathResource("init_vec.sql").getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    jdbcTemplate.execute(trimmed);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read init_vec.sql", e);
        }
    }
}
