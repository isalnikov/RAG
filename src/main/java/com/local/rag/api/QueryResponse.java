package com.local.rag.api;

import java.util.List;

/**
 * Ответ {@code POST /api/query}: сгенерированный текст и список источников.
 */
public class QueryResponse {

    /** Ответ LLM на русском языке. */
    private String answer;

    /** Список использованных фрагментов документов. */
    private List<SourceRef> sources;

    /** Конструктор по умолчанию. */
    public QueryResponse() {
    }

    /**
     * @param answer  текст ответа
     * @param sources список источников
     */
    public QueryResponse(String answer, List<SourceRef> sources) {
        this.answer = answer;
        this.sources = sources;
    }

    /**
     * @return ответ модели
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @param answer ответ модели
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * @return список источников
     */
    public List<SourceRef> getSources() {
        return sources;
    }

    /**
     * @param sources список источников
     */
    public void setSources(List<SourceRef> sources) {
        this.sources = sources;
    }
}
