package com.local.rag.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.local.rag.config.SqliteProperties;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class DatabaseInitializerTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private Connection connection;
    @Mock
    private Statement statement;

    @Test
    void initialize_withoutExtension_executesInitScript() {
        SqliteProperties properties = new SqliteProperties();
        properties.setVecExtensionPath("");
        DatabaseInitializer initializer = new DatabaseInitializer(dataSource, jdbcTemplate, properties);

        initializer.initialize();

        verify(jdbcTemplate, atLeastOnce()).execute(contains("CREATE TABLE IF NOT EXISTS chunks"));
        verify(jdbcTemplate, atLeastOnce()).execute(contains("vec_chunks"));
        org.mockito.Mockito.verifyNoInteractions(dataSource);
    }

    @Test
    void initialize_withExtension_loadsLibrary() throws SQLException {
        SqliteProperties properties = new SqliteProperties();
        properties.setVecExtensionPath("/tmp/libvec0.dylib");
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);

        DatabaseInitializer initializer = new DatabaseInitializer(dataSource, jdbcTemplate, properties);
        initializer.initialize();

        verify(statement).execute("SELECT load_extension('/tmp/libvec0.dylib')");
    }

    @Test
    void initialize_extensionSqlError_throwsIllegalState() throws SQLException {
        SqliteProperties properties = new SqliteProperties();
        properties.setVecExtensionPath("/bad/path.so");
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenThrow(new SQLException("load failed"));

        DatabaseInitializer initializer = new DatabaseInitializer(dataSource, jdbcTemplate, properties);

        org.assertj.core.api.Assertions.assertThatThrownBy(initializer::initialize)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sqlite-vec");
    }
}
