package com.local.rag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Параметры векторного поиска по индексу чанков.
 * <p>
 * Префикс конфигурации: {@code app.search}.
 */
@ConfigurationProperties(prefix = "app.search")
public class SearchProperties {

    /** Число ближайших чанков (top-K), возвращаемых из sqlite-vec. */
    private int topK;

    /**
     * @return значение top-K для ANN-поиска
     */
    public int getTopK() {
        return topK;
    }

    /**
     * @param topK значение top-K для ANN-поиска
     */
    public void setTopK(int topK) {
        this.topK = topK;
    }
}
