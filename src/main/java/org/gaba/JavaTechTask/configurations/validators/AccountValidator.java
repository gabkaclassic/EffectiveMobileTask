package org.gaba.JavaTechTask.configurations.validators;

import org.gaba.JavaTechTask.entities.Account;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Component
public class AccountValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+@\\w+\\.\\w+");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&+=_-]{8,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,128}$");

    public boolean validateAccount(Account account) {

        return checkPassword(account.getPassword())
                && checkEmail(account.getEmail())
                && checkUsername(account.getUsername());
    }

    private boolean checkPassword(String password) {
        return StringUtils.hasText(password)
                && PASSWORD_PATTERN.matcher(password).matches();
    }

    private boolean checkUsername(String username) {
        return StringUtils.hasText(username)
                && USERNAME_PATTERN.matcher(username).matches();
    }

    private boolean checkEmail(String email) {
        return StringUtils.hasText(email)
                && EMAIL_PATTERN.matcher(email).matches();
    }
}
