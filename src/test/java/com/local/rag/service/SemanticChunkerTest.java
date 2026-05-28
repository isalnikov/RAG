package com.local.rag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.local.rag.config.ChunkingProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SemanticChunkerTest {

    @Mock
    private OllamaService ollamaService;

    private SemanticChunker semanticChunker;

    @BeforeEach
    void setUp() {
        ChunkingProperties properties = new ChunkingProperties();
        properties.setMinSimilarity(0.7);
        properties.setMaxTokens(500);
        properties.setOverlapTokens(2);
        semanticChunker = new SemanticChunker(properties, ollamaService);
    }

    @Test
    void chunk_emptyText_returnsEmpty() {
        assertThat(semanticChunker.chunk("   ")).isEmpty();
    }

    @Test
    void chunk_singleSentence_returnsOneChunk() {
        when(ollamaService.getEmbedding(anyString())).thenReturn(new float[] {1f, 0f});
        List<String> chunks = semanticChunker.chunk("Это одно длинное предложение для проверки чанкера.");
        assertThat(chunks).hasSize(1);
    }

    @Test
    void chunk_codeBlock_treatedAsSingleUnit() {
        when(ollamaService.getEmbedding(anyString())).thenReturn(new float[] {1f, 0f});
        String code = "public class Foo {\n  return 1;\n}";
        List<String> chunks = semanticChunker.chunk(code);
        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0)).contains("public class");
    }

    @Test
    void chunk_lowSimilarity_splitsIntoMultiple() {
        when(ollamaService.getEmbedding("Первое предложение с текстом."))
                .thenReturn(new float[] {1f, 0f});
        when(ollamaService.getEmbedding("Второе предложение с другим смыслом."))
                .thenReturn(new float[] {0f, 1f});
        String text = "Первое предложение с текстом.\n\nВторое предложение с другим смыслом.";
        List<String> chunks = semanticChunker.chunk(text);
        assertThat(chunks.size()).isGreaterThanOrEqualTo(2);
    }
}
