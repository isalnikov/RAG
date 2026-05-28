package com.local.rag.api;

/**
 * Тело HTTP-запроса {@code POST /api/query}.
 */
public class QueryRequest {

    /** Вопрос пользователя на естественном языке. */
    private String question;

    /**
     * @return текст вопроса
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @param question текст вопроса
     */
    public void setQuestion(String question) {
        this.question = question;
    }
}
