package com.local.rag.service;

import com.local.rag.api.QueryResponse;
import com.local.rag.api.SourceRef;
import com.local.rag.config.SearchProperties;
import com.local.rag.model.ChunkWithDistance;
import com.local.rag.repository.ChunkRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * RAG-запрос: эмбеддинг вопроса → top-K чанков → ответ LLM.
 */
@Service
public class QueryService {

    private final ChunkRepository chunkRepository;
    private final OllamaService ollamaService;
    private final SearchProperties searchProperties;

    /**
     * @param chunkRepository  векторный поиск
     * @param ollamaService    эмбеддинг и генерация
     * @param searchProperties параметр top-K
     */
    public QueryService(ChunkRepository chunkRepository,
                        OllamaService ollamaService,
                        SearchProperties searchProperties) {
        this.chunkRepository = chunkRepository;
        this.ollamaService = ollamaService;
        this.searchProperties = searchProperties;
    }

    /**
     * Отвечает на вопрос пользователя по индексированным документам.
     *
     * @param question текст вопроса
     * @return ответ и ссылки на использованные чанки
     */
    public QueryResponse answer(String question) {
        float[] queryEmbedding = ollamaService.getEmbedding(question);
        List<ChunkWithDistance> nearest = chunkRepository.findSimilar(queryEmbedding, searchProperties.getTopK());
        List<String> contexts = nearest.stream().map(item -> item.getChunk().getContent()).toList();
        String answer = ollamaService.generateAnswer(question, contexts);
        List<SourceRef> sources = nearest.stream()
                .map(item -> new SourceRef(item.getChunk().getSource(), item.getChunk().getChunkIndex()))
                .toList();
        return new QueryResponse(answer, sources);
    }
}
