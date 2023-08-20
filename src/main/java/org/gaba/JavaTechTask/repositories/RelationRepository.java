package org.gaba.JavaTechTask.repositories;

import org.gaba.JavaTechTask.entities.Relation;
import org.gaba.JavaTechTask.entities.RelationType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RelationRepository extends ReactiveCrudRepository<Relation, String> {
    Mono<Relation> findByUserAndTargetAndRelationType(String user, String target, RelationType relationType);
    Mono<Relation> findByUserAndTarget(String user, String target);
}
