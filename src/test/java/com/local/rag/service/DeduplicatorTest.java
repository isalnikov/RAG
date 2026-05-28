package com.local.rag.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class DeduplicatorTest {

    private final Deduplicator deduplicator = new Deduplicator();

    @Test
    void unique_removesDuplicates_preservesOrder() {
        List<String> result = deduplicator.unique(List.of("a", "b", "a", "c", "b"));
        assertThat(result).containsExactly("a", "b", "c");
    }

    @Test
    void unique_emptyList_returnsEmpty() {
        assertThat(deduplicator.unique(List.of())).isEmpty();
    }
}
