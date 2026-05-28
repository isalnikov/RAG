package com.local.rag.api;

/**
 * Ссылка на фрагмент исходного документа, использованный при ответе.
 */
public class SourceRef {

    /** Имя файла-источника. */
    private String source;

    /** Индекс чанка внутри документа. */
    private int chunkIndex;

    /** Конструктор по умолчанию. */
    public SourceRef() {
    }

    /**
     * @param source     имя файла
     * @param chunkIndex индекс чанка
     */
    public SourceRef(String source, int chunkIndex) {
        this.source = source;
        this.chunkIndex = chunkIndex;
    }

    /**
     * @return имя источника
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source имя источника
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return индекс чанка
     */
    public int getChunkIndex() {
        return chunkIndex;
    }

    /**
     * @param chunkIndex индекс чанка
     */
    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }
}
