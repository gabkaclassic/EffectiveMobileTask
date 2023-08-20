package org.gaba.JavaTechTask.services;

import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.entities.Relation;
import org.gaba.JavaTechTask.entities.RelationType;
import org.gaba.JavaTechTask.repositories.RelationRepository;
import org.gaba.JavaTechTask.validators.AccountValidator;
import org.gaba.JavaTechTask.entities.Account;
import org.gaba.JavaTechTask.entities.Authority;
import org.gaba.JavaTechTask.repositories.AccountRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService implements ReactiveUserDetailsService {

    private final AccountRepository accountRepository;

    private final RelationRepository relationRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final AccountValidator accountValidator;
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public Mono<List<String>> findIdsByUsernames(List<String> usernames) {

        return  Flux.fromStream(usernames.stream())
                .flatMap(participant -> accountRepository.findByUsername(participant))
                .cast(Account.class)
                .map(participant -> participant.getId())
                .collectList();
    }

    public Mono<Boolean> isFriends(String accountId, String friend) {

        return accountRepository.existsById(accountId)
                .flatMap(accountResult -> {
                    if(!accountResult)
                        return Mono.just(false);

                    return accountRepository.findByUsername(friend)
                            .cast(Account.class)
                            .flatMap(user -> relationRepository.existsByUserAndTargetAndRelationType(accountId, user.getId(), RelationType.FRIEND));
                });

    }
    public Mono<String> login(Account account) {

        return accountRepository.findByUsername(account.getUsername())
                .cast(Account.class)
                
                .map(acc -> {
                    var correctPassword = passwordEncoder.matches(account.getPassword(), acc.getPassword());

                    if(correctPassword && acc.isEnabled())
                        return jwtService.generateToken(acc);

                    throw new InvalidPasswordException("Invalid password");
                });
    }

    public Mono<List<String>> registration(Account account) {

        return accountRepository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(account.getUsername(), account.getEmail())
                .map(exists -> {
                   if(exists)
                       return List.of("Account with this username or email already exists");

                   var errors = accountValidator.validateAccount(account);

                    if(!errors.isEmpty())
                        return errors;

                   account.setConfirmationCode(UUID.randomUUID().toString());
                   account.setPassword(passwordEncoder.encode(account.getPassword()));
                   account.setAuthorities(Set.of(Authority.USER));
                   accountRepository.save(account).subscribe();

                   return Collections.emptyList();
                });
    }

    public void sendRequest(String user_id, String targetUsername) {

        accountRepository.existsById(user_id).doOnNext(resultUser -> {
             accountRepository.findByUsername(targetUsername).cast(Account.class).doOnNext(target -> {
                        if(!resultUser || user_id.equals(target.getId()))
                            return;

                        var subscribeRelation = new Relation(user_id, target.getId(), RelationType.SUBSCRIBER);
                        var requestRelation = new Relation(target.getId(), user_id, RelationType.REQUEST);
                        relationRepository.save(subscribeRelation).then(relationRepository.save(requestRelation)).subscribe();
                    }).subscribe();
                }
        ).subscribe();
    }

    public void confirmRequest(String username, String target_id) {

        accountRepository.findByUsername(username).cast(Account.class).doOnNext(user -> {
                    accountRepository.existsById(target_id).doOnNext(resultTarget -> {

                        if(!resultTarget || target_id.equals(user.getId()))
                            return;

                        relationRepository.findByUserAndTargetAndRelationType(user.getId(), target_id, RelationType.SUBSCRIBER)
                                .doOnNext(relation -> {
                                    relation.setRelationType(RelationType.FRIEND);
                                    relationRepository.save(relation).subscribe();
                                }).then(relationRepository.findByUserAndTargetAndRelationType(target_id, user.getId(), RelationType.REQUEST))
                                .doOnNext(relation -> {
                                    relation.setRelationType(RelationType.FRIEND);
                                    relationRepository.save(relation).subscribe();
                                }).subscribe();
                    }).subscribe();
                }
        ).subscribe();
    }

    public void rejectRequest(String username, String target_id) {

        accountRepository.findByUsername(username).cast(Account.class).doOnNext(user -> accountRepository.existsById(target_id).doOnNext(resultTarget -> {
            if(!resultTarget || target_id.equals(user.getId()))
                return;

           relationRepository.findByUserAndTargetAndRelationType(target_id, user.getId(), RelationType.REQUEST)
                    .doOnNext(relation -> relationRepository.delete(relation).subscribe()).subscribe();
        }).subscribe()
        ).subscribe();
    }

    public void rejectFriend(String user_id, String targetUsername) {

        accountRepository.existsById(user_id).doOnNext(resultUser -> {
                    accountRepository.findByUsername(targetUsername).cast(Account.class).doOnNext(target -> {
                        if (!resultUser || user_id.equals(target.getId()))
                            return;

                        relationRepository.findByUserAndTargetAndRelationType(user_id, target.getId(), RelationType.FRIEND)
                                .doOnNext(relation -> {
                                    relation.setRelationType(RelationType.SUBSCRIBER);
                                    relationRepository.save(relation).subscribe();
                                })
                                .then(relationRepository.findByUserAndTargetAndRelationType(target.getId(), user_id, RelationType.FRIEND))
                                .doOnNext(relation -> relationRepository.delete(relation).subscribe()).subscribe();
                    }).subscribe();
                }
        ).subscribe();
    }

    public Mono<Boolean> existsById(String accountId) {
        return accountRepository.existsById(accountId.toString());
    }
}
