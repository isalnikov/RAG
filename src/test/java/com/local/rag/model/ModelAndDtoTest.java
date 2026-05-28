package com.local.rag.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.local.rag.api.IngestResponse;
import com.local.rag.api.QueryRequest;
import com.local.rag.api.QueryResponse;
import com.local.rag.api.SourceRef;
import java.util.List;
import org.junit.jupiter.api.Test;

class ModelAndDtoTest {

    @Test
    void chunk_gettersAndSetters() {
        Chunk chunk = new Chunk();
        chunk.setId("1");
        chunk.setDocId("d");
        chunk.setSource("s.pdf");
        chunk.setChunkIndex(2);
        chunk.setContent("text");

        assertThat(chunk.getId()).isEqualTo("1");
        assertThat(chunk.getDocId()).isEqualTo("d");
        assertThat(chunk.getSource()).isEqualTo("s.pdf");
        assertThat(chunk.getChunkIndex()).isEqualTo(2);
        assertThat(chunk.getContent()).isEqualTo("text");

        Chunk full = new Chunk("1", "d", "s.pdf", 2, "text");
        assertThat(full.getContent()).isEqualTo("text");
    }

    @Test
    void chunkWithDistance_exposesFields() {
        Chunk chunk = new Chunk("1", "d", "s", 0, "c");
        ChunkWithDistance cwd = new ChunkWithDistance(chunk, 0.5);
        assertThat(cwd.getChunk()).isSameAs(chunk);
        assertThat(cwd.getDistance()).isEqualTo(0.5);
    }

    @Test
    void apiDtos_roundTrip() {
        QueryRequest request = new QueryRequest();
        request.setQuestion("q");
        assertThat(request.getQuestion()).isEqualTo("q");

        SourceRef ref = new SourceRef("f.pdf", 1);
        assertThat(ref.getSource()).isEqualTo("f.pdf");
        ref.setChunkIndex(2);
        assertThat(ref.getChunkIndex()).isEqualTo(2);

        QueryResponse response = new QueryResponse("a", List.of(ref));
        response.setAnswer("b");
        response.setSources(List.of());
        assertThat(response.getAnswer()).isEqualTo("b");

        IngestResponse ingest = new IngestResponse("ok", 5, "doc");
        ingest.setStatus("done");
        ingest.setChunksStored(1);
        ingest.setDocumentId("x");
        assertThat(ingest.getStatus()).isEqualTo("done");
        assertThat(ingest.getChunksStored()).isEqualTo(1);
        assertThat(ingest.getDocumentId()).isEqualTo("x");
    }
}
