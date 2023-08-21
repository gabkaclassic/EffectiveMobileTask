package org.gaba.JavaTechTask.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.GrantedAuthority;

@Schema(description = "Роли пользователей")
public enum Authority implements GrantedAuthority {

    USER, BLOCKED, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
