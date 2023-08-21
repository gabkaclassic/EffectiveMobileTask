package org.gaba.JavaTechTask.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Set;

@Table("accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сущность пользователя")
public class Account implements UserDetails {

    @Id
    private String id;

    @Schema(description = "Имя пользователя")
    private String username;

    @Schema(description = "Пароль")
    @ToString.Exclude
    private String password;

    @Schema(description = "Электронная почта")
    @ToString.Exclude
    private String email;

    @Schema(description = "Код подтверждения почты (для будущего функционала)")
    private String confirmationCode;

    @Schema(description = "Роли пользователя")
    private Set<Authority> authorities = Set.of(Authority.USER);

    public Account(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !authorities.contains(Authority.BLOCKED);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !authorities.contains(Authority.BLOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !authorities.contains(Authority.BLOCKED);
    }

    @Override
    public boolean isEnabled() {
        return (confirmationCode == null || StringUtils.hasText(confirmationCode)) && !authorities.contains(Authority.BLOCKED);
    }
}
