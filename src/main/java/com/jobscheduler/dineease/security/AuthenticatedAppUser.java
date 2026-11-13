package com.jobscheduler.dineease.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AuthenticatedAppUser extends User {

    private final Long appUserId;

    public AuthenticatedAppUser(
            Long appUserId,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities) {

        super(username, password, authorities);
        this.appUserId = appUserId;
    }

    public Long getAppUserId() {
        return appUserId;
    }
}
