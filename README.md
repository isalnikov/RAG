# Local RAG (Spring Boot + SQLite + Ollama)

Полностью локальная RAG-система на Java 17 и Spring Boot 3.5 для CPU-окружения.

## Что реализовано

- Ingest PDF через `POST /api/documents/ingest`
- Извлечение текста (PDFBox), очистка и дедупликация чанков (MD5)
- Семантический чанкинг по сходству эмбеддингов предложений
- Эмбеддинги и генерация ответа через локальный Ollama API
- Хранение чанков в SQLite и ANN-поиск через `sqlite-vec`
- Query API `POST /api/query` с возвратом ответа и источников

## Требования

- Java 17
- Maven 3.9+
- [Ollama](https://ollama.com/) установлен локально
- Расширение `sqlite-vec` (`vec0` shared library) для вашей ОС

## Установка Ollama и моделей

```bash
brew install ollama
ollama serve
ollama pull jeffh/multilingual-e5-small
ollama pull saiga_mistral_7b
```

Если `saiga_mistral_7b` недоступна в вашем реестре, используйте совместимую русскоязычную модель (например, `qwen2:7b`), обновив `app.ollama.chat-model` в `application.yml`.

## Настройка sqlite-vec

1. Скачайте бинарник `vec0` под вашу платформу (например, `libvec0.dylib`/`libvec0.so`/`vec0.dll`).
2. Укажите путь в `src/main/resources/application.yml`:

```yaml
app:
  sqlite:
    vec-extension-path: /absolute/path/to/libvec0.dylib
```

## Запуск

```bash
mvn spring-boot:run
```

После запуска создастся `rag.db` в рабочей директории.

## API

### 1) Ingest PDF

```bash
curl -X POST "http://localhost:8080/api/documents/ingest" \
  -F "file=@/path/to/doc.pdf"
```

### 2) Query

```bash
curl -X POST "http://localhost:8080/api/query" \
  -H "Content-Type: application/json" \
  -d '{"question":"Сформулируй основные шаги интеграции sqlite-vec"}'
```

## Структура

- `src/main/java/com/local/rag/config` — конфигурация и properties
- `src/main/java/com/local/rag/service` — ingestion/chunking/ollama services
- `src/main/java/com/local/rag/repository` — SQLite + sqlite-vec доступ к данным
- `src/main/java/com/local/rag/api` — REST API и обработка ошибок
- `src/main/resources/init_vec.sql` — инициализация таблиц
