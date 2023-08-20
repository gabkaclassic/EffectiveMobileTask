CREATE TABLE IF NOT EXISTS posts (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    subject VARCHAR(256),
    content TEXT,
    author VARCHAR(36),
    files TEXT[]
);

ALTER TABLE posts ADD CONSTRAINT fk_post_account_uid
FOREIGN KEY (author) REFERENCES accounts(id);