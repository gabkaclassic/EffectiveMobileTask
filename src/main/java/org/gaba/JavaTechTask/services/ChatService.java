package org.gaba.JavaTechTask.services;

import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.entities.Account;
import org.gaba.JavaTechTask.entities.Chat;
import org.gaba.JavaTechTask.repositories.ChatRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final AccountService accountService;

    public Mono<String> create(String creatorId, Chat chat) {

        return accountService.isFriends(creatorId, chat.getParticipant())
                .flatMap(result -> {

                    if(!result)
                        return Mono.just(chat);

                    return accountService.findByUsername(chat.getParticipant())
                            .cast(Account.class)
                            .flatMap(participant -> {
                                chat.setCreator(creatorId);
                                chat.setParticipant(participant.getId());
                                return chatRepository.save(chat);
                            });

                })
                .mapNotNull(newChat -> newChat.getId());
    }

    public Mono<Chat> getChat(String account_id, String chatId) {

        return chatRepository.findById(chatId).flatMap(chat -> {
            return chat.getParticipant().equals(account_id) || chat.getCreator().equals(account_id) ?
                    Mono.just(chat) : Mono.empty();
        });
    }
}
