package com.local.rag.api;

import com.local.rag.service.IngestionService;
import com.local.rag.service.QueryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST API локальной RAG-системы: загрузка PDF и ответы на вопросы.
 */
@RestController
@RequestMapping("/api")
public class RagController {

    private final IngestionService ingestionService;
    private final QueryService queryService;

    /**
     * @param ingestionService сервис индексации документов
     * @param queryService     сервис RAG-запросов
     */
    public RagController(IngestionService ingestionService, QueryService queryService) {
        this.ingestionService = ingestionService;
        this.queryService = queryService;
    }

    /**
     * Принимает PDF, извлекает текст, строит чанки и сохраняет эмбеддинги.
     *
     * @param file поле multipart {@code file}
     * @return статус и число сохранённых чанков
     */
    @PostMapping(value = "/documents/ingest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IngestResponse ingest(@RequestParam("file") MultipartFile file) {
        IngestionService.IngestionResult result = ingestionService.ingest(file);
        return new IngestResponse("ok", result.storedChunks(), result.documentId());
    }

    /**
     * Отвечает на вопрос по проиндексированным документам.
     *
     * @param request JSON с полем {@code question}
     * @return ответ LLM и список источников
     * @throws IllegalArgumentException если {@code question} пустой
     */
    @PostMapping("/query")
    public QueryResponse query(@RequestBody QueryRequest request) {
        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new IllegalArgumentException("Поле question не должно быть пустым");
        }
        return queryService.answer(request.getQuestion());
    }
}
