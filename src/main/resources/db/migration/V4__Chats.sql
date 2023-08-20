CREATE TABLE IF NOT EXISTS chats (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    creator VARCHAR(36),
    participant VARCHAR(36)
);

ALTER TABLE chats ADD CONSTRAINT fk_chat_creator_uid
FOREIGN KEY (creator) REFERENCES accounts(id);

ALTER TABLE chats ADD CONSTRAINT uq_creator_participant
UNIQUE (creator, participant);

CREATE OR REPLACE FUNCTION check_insert_chats_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.creator = NEW.participant OR EXISTS (
        SELECT 1 FROM chats
        WHERE (creator = NEW.participant AND participant = NEW.creator)
    ) THEN
        RAISE EXCEPTION 'Invalid data for insertion';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER insert_chats_trigger
BEFORE INSERT ON chats
FOR EACH ROW
EXECUTE FUNCTION check_insert_chats_trigger();