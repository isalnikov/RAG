package com.local.rag.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class ConfigurationPropertiesTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(PropertiesTestConfig.class)
            .withPropertyValues(
                    "app.ollama.base-url=http://localhost:11434",
                    "app.ollama.embedding-model=embed",
                    "app.ollama.chat-model=chat",
                    "app.ollama.connect-timeout-ms=1000",
                    "app.ollama.read-timeout-ms=2000",
                    "app.chunk.min-similarity=0.75",
                    "app.chunk.max-tokens=400",
                    "app.chunk.overlap-tokens=40",
                    "app.search.top-k=5",
                    "app.sqlite.vec-extension-path=/vec/lib.so"
            );

    @EnableConfigurationProperties({
            OllamaProperties.class,
            ChunkingProperties.class,
            SearchProperties.class,
            SqliteProperties.class
    })
    static class PropertiesTestConfig {
    }

    @Test
    void propertiesBindCorrectly() {
        runner.run(context -> {
            OllamaProperties ollama = context.getBean(OllamaProperties.class);
            assertThat(ollama.getBaseUrl()).isEqualTo("http://localhost:11434");
            assertThat(ollama.getEmbeddingModel()).isEqualTo("embed");
            assertThat(ollama.getConnectTimeoutMs()).isEqualTo(1000);

            ChunkingProperties chunk = context.getBean(ChunkingProperties.class);
            assertThat(chunk.getMinSimilarity()).isEqualTo(0.75);
            assertThat(chunk.getMaxTokens()).isEqualTo(400);

            SearchProperties search = context.getBean(SearchProperties.class);
            assertThat(search.getTopK()).isEqualTo(5);

            SqliteProperties sqlite = context.getBean(SqliteProperties.class);
            assertThat(sqlite.getVecExtensionPath()).isEqualTo("/vec/lib.so");
        });
    }
}
