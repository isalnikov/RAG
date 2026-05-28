package com.local.rag.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Извлекает и очищает текст из PDF с помощью Apache PDFBox.
 * <p>
 * Удаляет повторяющиеся колонтитулы и короткие строки, не похожие на код.
 */
@Component
public class PdfExtractor {

    /**
     * Читает PDF из multipart-загрузки и возвращает очищенный текст.
     *
     * @param file загруженный PDF
     * @return нормализованный текст построчно
     * @throws IllegalArgumentException если файл не удаётся распарсить
     */
    public String extractText(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String raw = stripper.getText(document);
            return clean(raw);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse PDF file: " + file.getOriginalFilename(), e);
        }
    }

    private String clean(String rawText) {
        String normalized = rawText.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalized.split("\n");
        Map<String, Integer> frequency = new HashMap<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && trimmed.length() < 120) {
                frequency.put(trimmed, frequency.getOrDefault(trimmed, 0) + 1);
            }
        }

        List<String> result = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            boolean repeatedHeaderFooter = frequency.getOrDefault(trimmed, 0) > 2 && trimmed.length() < 80;
            if (repeatedHeaderFooter) {
                continue;
            }
            if (trimmed.length() < 20 && !looksLikeCode(trimmed)) {
                continue;
            }
            result.add(trimmed.replaceAll("\\s{2,}", " "));
        }
        return String.join("\n", result);
    }

    private boolean looksLikeCode(String line) {
        return line.contains("{")
                || line.contains("}")
                || line.contains(";")
                || line.contains("()")
                || line.contains("=>")
                || line.contains("==")
                || line.contains("public ")
                || line.contains("class ")
                || line.contains("def ");
    }
}
