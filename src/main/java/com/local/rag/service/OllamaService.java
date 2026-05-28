package com.local.rag.service;

import com.local.rag.config.OllamaProperties;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Клиент локального Ollama: эмбеддинги и генерация ответа в чате.
 * <p>
 * Использует OpenAI-совместимые эндпоинты {@code /api/embeddings} и {@code /api/chat}.
 */
@Service
public class OllamaService {

    private static final String SYSTEM_PROMPT =
            "Ты — ассистент для разработчиков. Отвечай по-русски, используя только предоставленный контекст. Не придумывай факты.";

    private final RestTemplate restTemplate;
    private final OllamaProperties properties;

    /**
     * @param restTemplate HTTP-клиент с настроенными таймаутами
     * @param properties   модели и базовый URL Ollama
     */
    public OllamaService(RestTemplate restTemplate, OllamaProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    /**
     * Запрашивает векторное представление текста и L2-нормализует результат.
     *
     * @param text исходный текст (предложение или чанк)
     * @return нормализованный вектор эмбеддинга
     * @throws OllamaUnavailableException если Ollama недоступен
     * @throws IllegalStateException      при ошибке разбора ответа
     */
    public float[] getEmbedding(String text) {
        try {
            JSONObject payload = new JSONObject()
                    .put("model", properties.getEmbeddingModel())
                    .put("prompt", text);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    properties.getBaseUrl() + "/api/embeddings",
                    createJsonRequest(payload.toString()),
                    String.class
            );
            JSONObject body = new JSONObject(response.getBody());
            JSONArray embedding = body.getJSONArray("embedding");
            float[] result = new float[embedding.length()];
            for (int i = 0; i < embedding.length(); i++) {
                result[i] = embedding.getNumber(i).floatValue();
            }
            return normalize(result);
        } catch (ResourceAccessException e) {
            throw new OllamaUnavailableException(
                    "Ollama недоступен. Проверьте, что сервис запущен на " + properties.getBaseUrl(), e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to get embedding from Ollama", e);
        }
    }

    /**
     * Генерирует ответ на вопрос с учётом найденных фрагментов контекста.
     *
     * @param question      вопрос пользователя
     * @param contextChunks тексты релевантных чанков
     * @return текст ответа модели
     * @throws OllamaUnavailableException если Ollama недоступен
     * @throws IllegalStateException      при ошибке разбора ответа
     */
    public String generateAnswer(String question, List<String> contextChunks) {
        try {
            StringBuilder contextBuilder = new StringBuilder();
            for (int i = 0; i < contextChunks.size(); i++) {
                contextBuilder.append("Фрагмент ").append(i + 1).append(":\n")
                        .append(contextChunks.get(i)).append("\n\n");
            }
            String userPrompt = "Контекст:\n" + contextBuilder + "\nВопрос:\n" + question;

            JSONArray messages = new JSONArray()
                    .put(new JSONObject().put("role", "system").put("content", SYSTEM_PROMPT))
                    .put(new JSONObject().put("role", "user").put("content", userPrompt));

            JSONObject payload = new JSONObject()
                    .put("model", properties.getChatModel())
                    .put("stream", false)
                    .put("messages", messages);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    properties.getBaseUrl() + "/api/chat",
                    createJsonRequest(payload.toString()),
                    String.class
            );
            JSONObject body = new JSONObject(response.getBody());
            return body.getJSONObject("message").getString("content");
        } catch (ResourceAccessException e) {
            throw new OllamaUnavailableException(
                    "Ollama недоступен. Проверьте, что сервис запущен на " + properties.getBaseUrl(), e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate answer from Ollama", e);
        }
    }

    private HttpEntity<String> createJsonRequest(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private float[] normalize(float[] vector) {
        double sumSquares = 0.0;
        for (float value : vector) {
            sumSquares += value * value;
        }
        double norm = Math.sqrt(sumSquares);
        if (norm == 0.0) {
            return vector;
        }
        List<Float> normalized = new ArrayList<>(vector.length);
        for (float value : vector) {
            normalized.add((float) (value / norm));
        }
        float[] out = new float[normalized.size()];
        for (int i = 0; i < normalized.size(); i++) {
            out[i] = normalized.get(i);
        }
        return out;
    }
}
