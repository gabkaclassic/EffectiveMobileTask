package org.gaba.JavaTechTask.services;

import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.validators.AccountValidator;
import org.gaba.JavaTechTask.entities.Account;
import org.gaba.JavaTechTask.entities.Authority;
import org.gaba.JavaTechTask.repositories.AccountRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService implements ReactiveUserDetailsService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final AccountValidator accountValidator;
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accountRepository.findByUsername(username);
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

    public Mono<Boolean> existsById(String authorId) {
        return accountRepository.existsById(authorId.toString());
    }
}
