package org.gaba.JavaTechTask.repositories;

import org.gaba.JavaTechTask.entities.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface AccountRepository extends ReactiveCrudRepository<Account, UUID> {
}
