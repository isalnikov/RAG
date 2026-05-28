package com.local.rag.api;

/**
 * Ответ {@code POST /api/documents/ingest} после загрузки PDF.
 */
public class IngestResponse {

    /** Статус операции (например {@code ok}). */
    private String status;

    /** Число сохранённых уникальных чанков. */
    private int chunksStored;

    /** Идентификатор загруженного документа. */
    private String documentId;

    /** Конструктор по умолчанию. */
    public IngestResponse() {
    }

    /**
     * @param status       статус
     * @param chunksStored число чанков
     * @param documentId   id документа
     */
    public IngestResponse(String status, int chunksStored, String documentId) {
        this.status = status;
        this.chunksStored = chunksStored;
        this.documentId = documentId;
    }

    /**
     * @return статус
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status статус
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return число сохранённых чанков
     */
    public int getChunksStored() {
        return chunksStored;
    }

    /**
     * @param chunksStored число сохранённых чанков
     */
    public void setChunksStored(int chunksStored) {
        this.chunksStored = chunksStored;
    }

    /**
     * @return идентификатор документа
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * @param documentId идентификатор документа
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
