package org.gaba.JavaTechTask.entities;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {

    USER, BLOCKED, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
