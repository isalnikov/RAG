package com.local.rag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки HTTP-клиента для локального Ollama API.
 * <p>
 * Префикс конфигурации: {@code app.ollama}.
 */
@ConfigurationProperties(prefix = "app.ollama")
public class OllamaProperties {

    /** Базовый URL Ollama, например {@code http://localhost:11434}. */
    private String baseUrl;

    /** Имя модели для эмбеддингов (например {@code jeffh/multilingual-e5-small}). */
    private String embeddingModel;

    /** Имя чат-модели для генерации ответа (например {@code saiga_mistral_7b}). */
    private String chatModel;

    /** Таймаут установки TCP-соединения в миллисекундах. */
    private int connectTimeoutMs;

    /** Таймаут чтения ответа в миллисекундах. */
    private int readTimeoutMs;

    /**
     * @return базовый URL Ollama
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * @param baseUrl базовый URL Ollama
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * @return имя embedding-модели
     */
    public String getEmbeddingModel() {
        return embeddingModel;
    }

    /**
     * @param embeddingModel имя embedding-модели
     */
    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * @return имя chat-модели
     */
    public String getChatModel() {
        return chatModel;
    }

    /**
     * @param chatModel имя chat-модели
     */
    public void setChatModel(String chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * @return таймаут подключения (мс)
     */
    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    /**
     * @param connectTimeoutMs таймаут подключения (мс)
     */
    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    /**
     * @return таймаут чтения (мс)
     */
    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    /**
     * @param readTimeoutMs таймаут чтения (мс)
     */
    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }
}
