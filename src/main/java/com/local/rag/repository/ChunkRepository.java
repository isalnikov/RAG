package com.local.rag.repository;

import com.local.rag.model.Chunk;
import com.local.rag.model.ChunkWithDistance;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Доступ к таблице {@code chunks} и ANN-индексу {@code vec_chunks} (sqlite-vec).
 */
@Repository
public class ChunkRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * @param jdbcTemplate шаблон JDBC для SQLite
     */
    public ChunkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Сохраняет метаданные чанка, BLOB-эмбеддинг и запись в виртуальной таблице vec0.
     *
     * @param chunk     сущность чанка
     * @param embedding вектор размерности 384
     * @throws IllegalStateException если не получен rowid после вставки в vec_chunks
     */
    @Transactional
    public void insertChunk(Chunk chunk, float[] embedding) {
        byte[] embeddingBlob = floatArrayToBytes(embedding);
        jdbcTemplate.update(
                "INSERT INTO chunks (id, doc_id, source, chunk_index, content, embedding) VALUES (?, ?, ?, ?, ?, ?)",
                chunk.getId(), chunk.getDocId(), chunk.getSource(), chunk.getChunkIndex(), chunk.getContent(), embeddingBlob);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO vec_chunks (embedding) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, toVecLiteral(embedding));
            return preparedStatement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to get vec row id for inserted embedding");
        }
        jdbcTemplate.update("INSERT INTO chunk_vec_map (chunk_id, vec_rowid) VALUES (?, ?)", chunk.getId(), key.longValue());
    }

    /**
     * Выполняет KNN-поиск по косинусной метрике через {@code MATCH} и {@code k}.
     *
     * @param queryEmbedding вектор запроса
     * @param topK           число ближайших соседей
     * @return чанки с расстоянием, отсортированные по близости
     */
    @Transactional(readOnly = true)
    public List<ChunkWithDistance> findSimilar(float[] queryEmbedding, int topK) {
        String sql = """
                SELECT c.id, c.doc_id, c.source, c.chunk_index, c.content, v.distance
                FROM vec_chunks v
                JOIN chunk_vec_map m ON m.vec_rowid = v.rowid
                JOIN chunks c ON c.id = m.chunk_id
                WHERE v.embedding MATCH ? AND k = ?
                ORDER BY v.distance
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Chunk chunk = new Chunk(
                    rs.getString("id"),
                    rs.getString("doc_id"),
                    rs.getString("source"),
                    rs.getInt("chunk_index"),
                    rs.getString("content")
            );
            return new ChunkWithDistance(chunk, rs.getDouble("distance"));
        }, toVecLiteral(queryEmbedding), topK);
    }

    private byte[] floatArrayToBytes(float[] values) {
        ByteBuffer buffer = ByteBuffer.allocate(values.length * Float.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        for (float value : values) {
            buffer.putFloat(value);
        }
        return buffer.array();
    }

    private String toVecLiteral(float[] vector) {
        List<String> values = new ArrayList<>(vector.length);
        for (float value : vector) {
            values.add(Float.toString(value));
        }
        return "[" + String.join(",", values) + "]";
    }
}
