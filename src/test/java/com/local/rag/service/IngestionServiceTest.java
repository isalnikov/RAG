package com.local.rag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.local.rag.repository.ChunkRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class IngestionServiceTest {

    @Mock
    private PdfExtractor pdfExtractor;
    @Mock
    private SemanticChunker semanticChunker;
    @Mock
    private Deduplicator deduplicator;
    @Mock
    private ChunkRepository chunkRepository;
    @Mock
    private OllamaService ollamaService;
    @Mock
    private MultipartFile file;

    @InjectMocks
    private IngestionService ingestionService;

    @Test
    void ingest_storesUniqueChunks() {
        when(file.getOriginalFilename()).thenReturn("manual.pdf");
        when(pdfExtractor.extractText(file)).thenReturn("текст");
        when(semanticChunker.chunk("текст")).thenReturn(List.of("chunk-a", "chunk-a", "chunk-b"));
        when(deduplicator.unique(List.of("chunk-a", "chunk-a", "chunk-b"))).thenReturn(List.of("chunk-a", "chunk-b"));
        when(ollamaService.getEmbedding(any())).thenReturn(new float[] {1f, 0f, 0f});

        IngestionService.IngestionResult result = ingestionService.ingest(file);

        assertThat(result.storedChunks()).isEqualTo(2);
        assertThat(result.documentId()).isNotBlank();
        verify(chunkRepository, times(2)).insertChunk(any(), any(float[].class));
    }

    @Test
    void ingest_nullFilename_usesUnknown() {
        when(file.getOriginalFilename()).thenReturn(null);
        when(pdfExtractor.extractText(file)).thenReturn("");
        when(semanticChunker.chunk("")).thenReturn(List.of());
        when(deduplicator.unique(List.of())).thenReturn(List.of());

        IngestionService.IngestionResult result = ingestionService.ingest(file);

        assertThat(result.storedChunks()).isZero();
    }
}
