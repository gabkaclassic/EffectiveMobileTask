package org.gaba.JavaTechTask.services;

import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import io.micrometer.observation.ObservationFilter;
import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.configurations.validators.AccountValidator;
import org.gaba.JavaTechTask.entities.Account;
import org.gaba.JavaTechTask.entities.Authority;
import org.gaba.JavaTechTask.entities.Credentials;
import org.gaba.JavaTechTask.repositories.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    public Mono<String> login(Credentials credentials) {

        return accountRepository.findByUsername(credentials.username()).log()
                .cast(Account.class)
                .log()
                .map(account -> {
                    var correctPassword = passwordEncoder.matches(credentials.password(), account.getPassword());

                    if(correctPassword && account.isEnabled())
                        return jwtService.generateToken(account);

                    throw new InvalidPasswordException("Invalid password");
                }).log();
    }

    public Mono<Account> test(Credentials credentials) {

        return accountRepository.findByUsername(credentials.username()).log().cast(Account.class);
    }

    public Mono<Boolean> registration(Account account) {

        return accountRepository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(account.getUsername(), account.getEmail())
                .map(exists -> {
                   if(exists || !accountValidator.validateAccount(account))
                        return false;

                   account.setConfirmationCode(UUID.randomUUID());
                   account.setPassword(passwordEncoder.encode(account.getPassword()));
                   account.setAuthorities(Set.of(Authority.USER));
                   accountRepository.save(account).subscribe();

                   return true;
                });
    }
}
