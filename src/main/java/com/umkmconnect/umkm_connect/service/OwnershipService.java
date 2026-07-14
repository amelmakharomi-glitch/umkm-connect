package com.umkmconnect.umkm_connect.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.umkmconnect.umkm_connect.entity.Role;
import com.umkmconnect.umkm_connect.entity.Umkm;
import com.umkmconnect.umkm_connect.entity.User;

@Service
public class OwnershipService {

    private final UserService userService;
    private final UmkmService umkmService;

    public OwnershipService(
            UserService userService,
            UmkmService umkmService
    ) {
        this.userService = userService;
        this.umkmService = umkmService;
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()) {
            return null;
        }

        return userService
                .getUserByEmail(authentication.getName())
                .orElse(null);
    }

    public boolean isAdmin(Authentication authentication) {
        User user = getCurrentUser(authentication);

        return user != null && user.getRole() == Role.ADMIN;
    }

    public boolean canManageUmkm(
            Authentication authentication,
            Long umkmId
    ) {
        User user = getCurrentUser(authentication);

        if (user == null || user.getRole() != Role.UMKM) {
            return false;
        }

        Umkm umkm = umkmService
                .getUmkmById(umkmId)
                .orElse(null);

        return umkm != null
                && umkm.getUser() != null
                && umkm.getUser().getId().equals(user.getId());
    }
}