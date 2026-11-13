package com.jobscheduler.dineease.service;

import com.jobscheduler.dineease.entity.AppUser;
import com.jobscheduler.dineease.repository.AppUserRepository;
import com.jobscheduler.dineease.security.AuthenticatedAppUser;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;

public class DatabaseUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public DatabaseUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        AppUser appUser = appUserRepository.findByUsername(username)
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found or disabled"));

        var authorities = appUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(
                        "ROLE_" + role.getRoleName()))
                .collect(Collectors.toSet());

        return new AuthenticatedAppUser(
                appUser.getId(),
                appUser.getUsername(),
                appUser.getPasswordHash(),
                authorities
        );
    }
}
