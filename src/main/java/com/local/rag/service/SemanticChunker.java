package com.local.rag.service;

import com.local.rag.config.ChunkingProperties;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Семантический чанкинг: группировка предложений по сходству эмбеддингов и лимиту токенов.
 */
@Component
public class SemanticChunker {

    private final ChunkingProperties properties;
    private final OllamaService ollamaService;

    /**
     * @param properties   пороги сходства, размера и перекрытия чанков
     * @param ollamaService сервис эмбеддингов предложений
     */
    public SemanticChunker(ChunkingProperties properties, OllamaService ollamaService) {
        this.properties = properties;
        this.ollamaService = ollamaService;
    }

    /**
     * Разбивает текст на семантически связные чанки с перекрытием.
     *
     * @param text очищенный текст документа
     * @return список текстов чанков (может быть пустым)
     */
    public List<String> chunk(String text) {
        List<String> sentences = splitIntoSentences(text);
        if (sentences.isEmpty()) {
            return List.of();
        }

        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        float[] previousEmbedding = null;
        int currentTokens = 0;

        for (String sentence : sentences) {
            float[] embedding = ollamaService.getEmbedding(sentence);
            int sentenceTokens = estimateTokens(sentence);

            boolean splitBySimilarity = previousEmbedding != null
                    && cosineSimilarity(previousEmbedding, embedding) < properties.getMinSimilarity();
            boolean splitBySize = currentTokens + sentenceTokens > properties.getMaxTokens();

            if ((splitBySimilarity || splitBySize) && !current.isEmpty()) {
                chunks.add(current.toString().trim());
                String overlap = tailTokens(current.toString(), properties.getOverlapTokens());
                current = new StringBuilder(overlap);
                currentTokens = estimateTokens(overlap);
            }

            if (!current.isEmpty() && current.charAt(current.length() - 1) != '\n') {
                current.append(' ');
            }
            current.append(sentence);
            currentTokens += sentenceTokens;
            previousEmbedding = embedding;
        }

        if (!current.isEmpty()) {
            chunks.add(current.toString().trim());
        }
        return chunks;
    }

    private List<String> splitIntoSentences(String text) {
        List<String> result = new ArrayList<>();
        for (String block : text.split("\\n{2,}")) {
            String trimmedBlock = block.trim();
            if (trimmedBlock.isEmpty()) {
                continue;
            }
            if (looksLikeCodeBlock(trimmedBlock)) {
                result.add(trimmedBlock);
                continue;
            }
            BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.forLanguageTag("ru"));
            iterator.setText(trimmedBlock);
            int start = iterator.first();
            for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
                String sentence = trimmedBlock.substring(start, end).trim();
                if (!sentence.isEmpty()) {
                    result.add(sentence);
                }
            }
        }
        return result;
    }

    private boolean looksLikeCodeBlock(String text) {
        String[] lines = text.split("\\n");
        int codeLikeLines = 0;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.contains("{")
                    || trimmed.contains("}")
                    || trimmed.contains(";")
                    || trimmed.matches(".*\\b(class|public|private|if|for|while|return)\\b.*")) {
                codeLikeLines++;
            }
        }
        return codeLikeLines >= Math.max(2, lines.length / 2);
    }

    private int estimateTokens(String text) {
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        return (int) Math.ceil(words / 0.75d);
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) {
            return 0.0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private String tailTokens(String text, int overlapTokens) {
        if (overlapTokens <= 0) {
            return "";
        }
        String[] words = text.trim().split("\\s+");
        int start = Math.max(0, words.length - overlapTokens);
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < words.length; i++) {
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(words[i]);
        }
        return sb.toString();
    }
}
