package com.local.rag.service;

/**
 * Исключение, выбрасываемое при недоступности локального Ollama API.
 * <p>
 * Обрабатывается {@link com.local.rag.api.ApiExceptionHandler} с HTTP 503.
 */
public class OllamaUnavailableException extends RuntimeException {

    /**
     * @param message понятное сообщение для клиента API
     * @param cause   исходная ошибка сети (например {@code ResourceAccessException})
     */
    public OllamaUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
