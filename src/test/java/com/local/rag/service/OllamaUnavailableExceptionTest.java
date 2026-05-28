package com.local.rag.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OllamaUnavailableExceptionTest {

    @Test
    void exceptionPreservesMessageAndCause() {
        RuntimeException cause = new RuntimeException("root");
        OllamaUnavailableException ex = new OllamaUnavailableException("msg", cause);
        assertThat(ex.getMessage()).isEqualTo("msg");
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
