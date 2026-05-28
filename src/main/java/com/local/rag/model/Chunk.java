package com.local.rag.model;

/**
 * Сущность текстового фрагмента (чанка), сохранённого в индексе RAG.
 */
public class Chunk {

    /** Уникальный идентификатор чанка (UUID). */
    private String id;

    /** Идентификатор родительского документа. */
    private String docId;

    /** Имя исходного файла (например PDF). */
    private String source;

    /** Порядковый номер чанка внутри документа. */
    private int chunkIndex;

    /** Текстовое содержимое чанка. */
    private String content;

    /** Конструктор по умолчанию для сериализации и ORM. */
    public Chunk() {
    }

    /**
     * Создаёт чанк со всеми полями.
     *
     * @param id         идентификатор чанка
     * @param docId      идентификатор документа
     * @param source     имя источника
     * @param chunkIndex индекс чанка
     * @param content    текст чанка
     */
    public Chunk(String id, String docId, String source, int chunkIndex, String content) {
        this.id = id;
        this.docId = docId;
        this.source = source;
        this.chunkIndex = chunkIndex;
        this.content = content;
    }

    /**
     * @return идентификатор чанка
     */
    public String getId() {
        return id;
    }

    /**
     * @param id идентификатор чанка
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return идентификатор документа
     */
    public String getDocId() {
        return docId;
    }

    /**
     * @param docId идентификатор документа
     */
    public void setDocId(String docId) {
        this.docId = docId;
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
     * @return индекс чанка в документе
     */
    public int getChunkIndex() {
        return chunkIndex;
    }

    /**
     * @param chunkIndex индекс чанка в документе
     */
    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    /**
     * @return текст чанка
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content текст чанка
     */
    public void setContent(String content) {
        this.content = content;
    }
}
