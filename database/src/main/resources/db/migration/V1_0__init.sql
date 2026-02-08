CREATE TABLE post
(
    id         UUID                 DEFAULT uuidv7() PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    body       TEXT        NOT NULL
);
