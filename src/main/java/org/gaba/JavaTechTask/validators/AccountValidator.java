package org.gaba.JavaTechTask.validators;

import org.gaba.JavaTechTask.entities.Account;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class AccountValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+@\\w+\\.\\w+");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&+=_-]{8,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,128}$");

    public List<String> validateAccount(Account account) {

        var errors = new ArrayList<String>();

        if(!checkPassword(account.getPassword()))
            errors.add("Password must contains numbers, special symbols (minimum 8 symbols)");
        if(!checkEmail(account.getEmail()))
            errors.add("Invalid email");
        if(!checkUsername(account.getUsername()))
            errors.add("Invalid username - 3-128 symbols, only latin letters, '_' and '-'");

        return errors;
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
