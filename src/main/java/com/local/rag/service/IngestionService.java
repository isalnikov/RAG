package com.local.rag.service;

import com.local.rag.model.Chunk;
import com.local.rag.repository.ChunkRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Пайплайн загрузки документа: PDF → чанки → эмбеддинги → SQLite.
 */
@Service
public class IngestionService {

    private final PdfExtractor pdfExtractor;
    private final SemanticChunker semanticChunker;
    private final Deduplicator deduplicator;
    private final ChunkRepository chunkRepository;
    private final OllamaService ollamaService;

    /**
     * @param pdfExtractor      извлечение текста из PDF
     * @param semanticChunker   семантическая нарезка
     * @param deduplicator      удаление дубликатов
     * @param chunkRepository   сохранение в БД
     * @param ollamaService     эмбеддинги чанков
     */
    public IngestionService(PdfExtractor pdfExtractor,
                            SemanticChunker semanticChunker,
                            Deduplicator deduplicator,
                            ChunkRepository chunkRepository,
                            OllamaService ollamaService) {
        this.pdfExtractor = pdfExtractor;
        this.semanticChunker = semanticChunker;
        this.deduplicator = deduplicator;
        this.chunkRepository = chunkRepository;
        this.ollamaService = ollamaService;
    }

    /**
     * Индексирует один PDF-файл в векторное хранилище.
     *
     * @param file multipart PDF
     * @return идентификатор документа и число сохранённых чанков
     */
    public IngestionResult ingest(MultipartFile file) {
        String documentId = UUID.randomUUID().toString();
        String source = file.getOriginalFilename() == null ? "unknown.pdf" : file.getOriginalFilename();

        String cleanedText = pdfExtractor.extractText(file);
        List<String> semanticChunks = semanticChunker.chunk(cleanedText);
        List<String> uniqueChunks = deduplicator.unique(semanticChunks);

        int stored = 0;
        for (int i = 0; i < uniqueChunks.size(); i++) {
            String content = uniqueChunks.get(i);
            float[] embedding = ollamaService.getEmbedding(content);
            Chunk chunk = new Chunk(UUID.randomUUID().toString(), documentId, source, i, content);
            chunkRepository.insertChunk(chunk, embedding);
            stored++;
        }
        return new IngestionResult(documentId, stored);
    }

    /**
     * Результат операции ingest.
     *
     * @param documentId   UUID документа
     * @param storedChunks число записанных чанков
     */
    public record IngestionResult(String documentId, int storedChunks) {
    }
}
