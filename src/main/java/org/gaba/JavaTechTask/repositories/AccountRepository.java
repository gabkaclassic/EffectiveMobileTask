package org.gaba.JavaTechTask.repositories;

import org.gaba.JavaTechTask.entities.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;
@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, String> {
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);
    Mono<UserDetails> findByUsername(String username);
}
