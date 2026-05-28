package com.local.rag.api;

import com.local.rag.service.OllamaUnavailableException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальная обработка ошибок REST API с понятными сообщениями на русском/английском.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Ollama не запущен или недоступен по сети.
     *
     * @param exception исключение недоступности
     * @return HTTP 503 и поле {@code error}
     */
    @ExceptionHandler(OllamaUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleOllamaUnavailable(OllamaUnavailableException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", exception.getMessage()));
    }

    /**
     * Некорректный запрос или ошибка бизнес-логики.
     *
     * @param exception исключение валидации или состояния
     * @return HTTP 400 и поле {@code error}
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }
}
