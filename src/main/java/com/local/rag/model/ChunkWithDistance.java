package com.local.rag.model;

/**
 * Результат ANN-поиска: чанк и расстояние до вектора запроса.
 */
public class ChunkWithDistance {

    private final Chunk chunk;
    private final double distance;

    /**
     * @param chunk    найденный чанк
     * @param distance метрика расстояния из sqlite-vec (меньше — ближе)
     */
    public ChunkWithDistance(Chunk chunk, double distance) {
        this.chunk = chunk;
        this.distance = distance;
    }

    /**
     * @return найденный чанк
     */
    public Chunk getChunk() {
        return chunk;
    }

    /**
     * @return расстояние до запроса
     */
    public double getDistance() {
        return distance;
    }
}
