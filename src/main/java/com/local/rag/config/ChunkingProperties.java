package com.local.rag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Параметры семантического чанкинга документов.
 * <p>
 * Префикс конфигурации: {@code app.chunk}.
 */
@ConfigurationProperties(prefix = "app.chunk")
public class ChunkingProperties {

    /** Минимальное косинусное сходство соседних предложений, ниже — новый чанк. */
    private double minSimilarity;

    /** Максимальный размер чанка в условных токенах. */
    private int maxTokens;

    /** Размер перекрытия между соседними чанками в токенах. */
    private int overlapTokens;

    /**
     * @return порог сходства для разрыва чанка
     */
    public double getMinSimilarity() {
        return minSimilarity;
    }

    /**
     * @param minSimilarity порог сходства (0..1)
     */
    public void setMinSimilarity(double minSimilarity) {
        this.minSimilarity = minSimilarity;
    }

    /**
     * @return максимум токенов в чанке
     */
    public int getMaxTokens() {
        return maxTokens;
    }

    /**
     * @param maxTokens максимум токенов в чанке
     */
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    /**
     * @return размер перекрытия в токенах
     */
    public int getOverlapTokens() {
        return overlapTokens;
    }

    /**
     * @param overlapTokens размер перекрытия в токенах
     */
    public void setOverlapTokens(int overlapTokens) {
        this.overlapTokens = overlapTokens;
    }
}
