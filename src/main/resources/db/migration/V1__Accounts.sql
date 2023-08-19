
CREATE TABLE IF NOT EXISTS accounts (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(128) UNIQUE,
    password VARCHAR(128),
    confirmation_code VARCHAR(128),
    email VARCHAR(128) UNIQUE,
    authorities TEXT[]
);