package com.local.rag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.local.rag.support.TestPdfFactory;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class PdfExtractorTest {

    @Mock
    private MultipartFile multipartFile;

    private final PdfExtractor pdfExtractor = new PdfExtractor();

    @BeforeEach
    void setUp() throws IOException {
        byte[] pdf = TestPdfFactory.createPdf(
                "Это достаточно длинная строка для извлечения из PDF документа.",
                "Вторая строка с содержательным текстом для теста RAG системы."
        );
        when(multipartFile.getBytes()).thenReturn(pdf);
        when(multipartFile.getOriginalFilename()).thenReturn("doc.pdf");
    }

    @Test
    void extractText_validPdf_returnsCleanedText() {
        String text = pdfExtractor.extractText(multipartFile);
        assertThat(text).contains("достаточно длинная строка");
        assertThat(text).contains("Вторая строка");
    }

    @Test
    void extractText_invalidPdf_throwsIllegalArgument() throws IOException {
        when(multipartFile.getBytes()).thenReturn(new byte[] {1, 2, 3});
        assertThatThrownBy(() -> pdfExtractor.extractText(multipartFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("doc.pdf");
    }
}
