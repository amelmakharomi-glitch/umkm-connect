package com.umkmconnect.umkm_connect.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umkmconnect.umkm_connect.entity.Role;
import com.umkmconnect.umkm_connect.entity.User;
import com.umkmconnect.umkm_connect.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable Long id
    ) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUmkm(
            @RequestBody User userRequest
    ) {
        String validationError = validateRegistration(userRequest);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", validationError)
            );
        }

        String email = userRequest
                .getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userService.emailSudahTerdaftar(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("message", "Email sudah terdaftar.")
            );
        }

        User user = new User();
        user.setName(userRequest.getName().trim());
        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(userRequest.getPassword())
        );

        // Registrasi publik hanya boleh membuat akun UMKM.
        user.setRole(Role.UMKM);

        User savedUser = userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Registrasi akun UMKM berhasil.",
                        "user", savedUser
                )
        );
    }

    private String validateRegistration(User user) {
        if (user.getName() == null
                || user.getName().trim().isEmpty()) {
            return "Nama wajib diisi.";
        }

        if (user.getEmail() == null
                || user.getEmail().trim().isEmpty()) {
            return "Email wajib diisi.";
        }

        String email = user.getEmail().trim();

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )) {
            return "Format email tidak valid.";
        }

        if (user.getPassword() == null
                || user.getPassword().length() < 8) {
            return "Password minimal 8 karakter.";
        }

        return null;
    }
}