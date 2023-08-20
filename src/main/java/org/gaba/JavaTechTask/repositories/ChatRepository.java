package org.gaba.JavaTechTask.repositories;

import org.gaba.JavaTechTask.entities.Chat;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ChatRepository extends ReactiveCrudRepository<Chat, String> {
    Chat findByCreatorAndParticipant(String creator, String participant);
}
