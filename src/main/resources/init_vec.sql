CREATE TABLE IF NOT EXISTS chunks (
    id TEXT PRIMARY KEY,
    doc_id TEXT NOT NULL,
    source TEXT NOT NULL,
    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    embedding BLOB NOT NULL
);

CREATE VIRTUAL TABLE IF NOT EXISTS vec_chunks USING vec0(embedding float[384]);

CREATE TABLE IF NOT EXISTS chunk_vec_map (
    chunk_id TEXT PRIMARY KEY,
    vec_rowid INTEGER NOT NULL UNIQUE,
    FOREIGN KEY (chunk_id) REFERENCES chunks(id)
);
