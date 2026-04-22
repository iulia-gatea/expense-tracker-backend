package org.example.security;

import org.example.model.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final Long userId;
    private final AppUser appUser;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long userId, AppUser appUser) {
        super(username, password, authorities);
        this.userId = userId;
        this.appUser = appUser;
    }

    public Long getUserId() {
        return userId;
    }

    public AppUser getAppUser() {
        return appUser;
    }
}
