package org.gaba.JavaTechTask.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Table("accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements UserDetails {

    @Id
    private UUID id;

    private String username;

    private String password;

    private String email;

    private UUID confirmationCode;

    private Set<Authority> authorities;

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
        return (confirmationCode == null || StringUtils.hasText(confirmationCode.toString())) && !authorities.contains(Authority.BLOCKED);
    }
}
