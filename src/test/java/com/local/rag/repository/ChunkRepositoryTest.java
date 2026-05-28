package com.local.rag.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.local.rag.model.Chunk;
import com.local.rag.model.ChunkWithDistance;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@ExtendWith(MockitoExtension.class)
class ChunkRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ChunkRepository chunkRepository;

    @Test
    void insertChunk_success_insertsThreeTimes() {
        Chunk chunk = new Chunk("id-1", "doc-1", "file.pdf", 0, "text");
        float[] embedding = {0.1f, 0.2f, 0.3f};

        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenAnswer(invocation -> {
            KeyHolder holder = invocation.getArgument(1);
            Map<String, Object> keys = new HashMap<>();
            keys.put("id", 42L);
            holder.getKeyList().add(keys);
            return 1;
        });

        chunkRepository.insertChunk(chunk, embedding);

        verify(jdbcTemplate).update(
                eq("INSERT INTO chunks (id, doc_id, source, chunk_index, content, embedding) VALUES (?, ?, ?, ?, ?, ?)"),
                eq("id-1"), eq("doc-1"), eq("file.pdf"), eq(0), eq("text"), any(byte[].class));
        verify(jdbcTemplate).update(eq("INSERT INTO chunk_vec_map (chunk_id, vec_rowid) VALUES (?, ?)"), eq("id-1"), eq(42L));
    }

    @Test
    void insertChunk_missingRowId_throwsIllegalState() {
        Chunk chunk = new Chunk("id-1", "doc-1", "file.pdf", 0, "text");
        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(1);

        assertThatThrownBy(() -> chunkRepository.insertChunk(chunk, new float[] {1f}))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("vec row id");
    }

    @Test
    @SuppressWarnings("unchecked")
    void findSimilar_returnsMappedChunks() {
        Chunk chunk = new Chunk("c1", "d1", "s.pdf", 1, "body");
        ChunkWithDistance expected = new ChunkWithDistance(chunk, 0.12);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), eq(5)))
                .thenReturn(List.of(expected));

        List<ChunkWithDistance> result = chunkRepository.findSimilar(new float[] {1f, 0f}, 5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChunk().getContent()).isEqualTo("body");
        ArgumentCaptor<String> vecCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), vecCaptor.capture(), eq(5));
        assertThat(vecCaptor.getValue()).startsWith("[");
    }
}
