package com.local.rag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки SQLite и динамической загрузки расширения sqlite-vec.
 * <p>
 * Префикс конфигурации: {@code app.sqlite}.
 */
@ConfigurationProperties(prefix = "app.sqlite")
public class SqliteProperties {

    /**
     * Абсолютный путь к shared library {@code vec0} ({@code .so}/{@code .dylib}/{@code .dll}).
     * Пустое значение — расширение не загружается (ожидается предзагрузка или отсутствие vec0).
     */
    private String vecExtensionPath;

    /**
     * @return путь к библиотеке sqlite-vec
     */
    public String getVecExtensionPath() {
        return vecExtensionPath;
    }

    /**
     * @param vecExtensionPath путь к библиотеке sqlite-vec
     */
    public void setVecExtensionPath(String vecExtensionPath) {
        this.vecExtensionPath = vecExtensionPath;
    }
}
