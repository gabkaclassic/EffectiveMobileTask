CREATE TABLE IF NOT EXISTS relations (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    sender VARCHAR(36),
    target VARCHAR(36),
    relation_type VARCHAR(128)
);

ALTER TABLE relations ADD CONSTRAINT uq_user_target
UNIQUE (sender, target);