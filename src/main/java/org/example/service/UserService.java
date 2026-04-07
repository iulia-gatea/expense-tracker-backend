package org.example.service;

import org.example.model.AppUser;

import java.util.Optional;

public interface UserService {
    AppUser saveUser(AppUser user);
    AppUser findByUsename(String username);
    Optional<AppUser> findUserById(Long id);
}
