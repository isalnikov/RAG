package com.local.rag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.local.rag.config.OllamaProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class OllamaServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private OllamaProperties properties;
    private OllamaService ollamaService;

    @BeforeEach
    void setUp() {
        properties = new OllamaProperties();
        properties.setBaseUrl("http://localhost:11434");
        properties.setEmbeddingModel("embed-model");
        properties.setChatModel("chat-model");
        ollamaService = new OllamaService(restTemplate, properties);
    }

    @Test
    void getEmbedding_success_returnsNormalizedVector() {
        when(restTemplate.postForEntity(
                eq("http://localhost:11434/api/embeddings"),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"embedding\":[3,4]}"));

        float[] embedding = ollamaService.getEmbedding("текст");
        assertThat(embedding).hasSize(2);
        double norm = Math.sqrt(embedding[0] * embedding[0] + embedding[1] * embedding[1]);
        assertThat(norm).isCloseTo(1.0, org.assertj.core.data.Offset.offset(1e-5));
    }

    @Test
    void getEmbedding_networkError_throwsOllamaUnavailable() {
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenThrow(new ResourceAccessException("connection refused"));

        assertThatThrownBy(() -> ollamaService.getEmbedding("x"))
                .isInstanceOf(OllamaUnavailableException.class)
                .hasMessageContaining("Ollama недоступен");
    }

    @Test
    void getEmbedding_invalidJson_throwsIllegalState() {
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"no_embedding\":true}"));

        assertThatThrownBy(() -> ollamaService.getEmbedding("x"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void generateAnswer_success_returnsContent() {
        when(restTemplate.postForEntity(
                eq("http://localhost:11434/api/chat"),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"message\":{\"content\":\"Ответ\"}}"));

        String answer = ollamaService.generateAnswer("вопрос", List.of("контекст"));
        assertThat(answer).isEqualTo("Ответ");
    }

    @Test
    void generateAnswer_networkError_throwsOllamaUnavailable() {
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenThrow(new ResourceAccessException("timeout"));

        assertThatThrownBy(() -> ollamaService.generateAnswer("q", List.of()))
                .isInstanceOf(OllamaUnavailableException.class);
    }
}
