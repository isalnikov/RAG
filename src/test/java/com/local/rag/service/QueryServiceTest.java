package com.local.rag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.local.rag.api.QueryResponse;
import com.local.rag.config.SearchProperties;
import com.local.rag.model.Chunk;
import com.local.rag.model.ChunkWithDistance;
import com.local.rag.repository.ChunkRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QueryServiceTest {

    @Mock
    private ChunkRepository chunkRepository;
    @Mock
    private OllamaService ollamaService;

    private QueryService queryService;

    @BeforeEach
    void setUp() {
        SearchProperties searchProperties = new SearchProperties();
        searchProperties.setTopK(3);
        queryService = new QueryService(chunkRepository, ollamaService, searchProperties);
    }

    @Test
    void answer_returnsResponseWithSources() {
        when(ollamaService.getEmbedding("вопрос")).thenReturn(new float[] {0.1f, 0.2f});
        Chunk chunk = new Chunk("c1", "d1", "doc.pdf", 0, "контент");
        when(chunkRepository.findSimilar(any(), eq(3)))
                .thenReturn(List.of(new ChunkWithDistance(chunk, 0.05)));
        when(ollamaService.generateAnswer("вопрос", List.of("контент"))).thenReturn("ответ");

        QueryResponse response = queryService.answer("вопрос");

        assertThat(response.getAnswer()).isEqualTo("ответ");
        assertThat(response.getSources()).hasSize(1);
        assertThat(response.getSources().get(0).getSource()).isEqualTo("doc.pdf");
        verify(chunkRepository).findSimilar(any(), eq(3));
    }
}
