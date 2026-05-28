package com.local.rag.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.local.rag.service.OllamaUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handleOllamaUnavailable_returns503() {
        ResponseEntity<java.util.Map<String, String>> response = handler.handleOllamaUnavailable(
                new OllamaUnavailableException("Ollama down", new RuntimeException("cause")));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).containsEntry("error", "Ollama down");
    }

    @Test
    void handleBadRequest_returns400() {
        ResponseEntity<java.util.Map<String, String>> response =
                handler.handleBadRequest(new IllegalArgumentException("bad input"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "bad input");
    }
}
