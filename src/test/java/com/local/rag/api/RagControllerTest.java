package com.local.rag.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.local.rag.service.IngestionService;
import com.local.rag.service.QueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {RagController.class, ApiExceptionHandler.class})
class RagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngestionService ingestionService;
    @MockBean
    private QueryService queryService;

    @Test
    void ingest_returnsOkPayload() throws Exception {
        when(ingestionService.ingest(any())).thenReturn(new IngestionService.IngestionResult("doc-1", 3));
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[] {1});

        mockMvc.perform(multipart("/api/documents/ingest").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.chunksStored").value(3))
                .andExpect(jsonPath("$.documentId").value("doc-1"));
    }

    @Test
    void query_validQuestion_returnsAnswer() throws Exception {
        when(queryService.answer("что такое RAG?"))
                .thenReturn(new QueryResponse("ответ", java.util.List.of(new SourceRef("a.pdf", 0))));

        mockMvc.perform(post("/api/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"что такое RAG?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("ответ"))
                .andExpect(jsonPath("$.sources[0].source").value("a.pdf"));
    }

    @Test
    void query_blankQuestion_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Поле question не должно быть пустым"));
    }
}
