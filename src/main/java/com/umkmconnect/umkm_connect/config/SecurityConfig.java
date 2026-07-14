package com.umkmconnect.umkm_connect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Halaman dan endpoint publik
                .requestMatchers(
                        "/",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/users/register"
                ).permitAll()

                // Data publik yang boleh dilihat masyarakat
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/umkms/**",
                        "/api/products/**",
                        "/api/categories/**"
                ).permitAll()

                // Daftar akun hanya boleh dilihat Admin
                .requestMatchers("/api/users/**")
                .hasRole("ADMIN")

                // Verifikasi status UMKM hanya oleh Admin
                .requestMatchers(
                        HttpMethod.PATCH,
                        "/api/umkms/*/status"
                ).hasRole("ADMIN")

                // Penghapusan data UMKM hanya oleh Admin
                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/umkms/**"
                ).hasRole("ADMIN")

                // Profil UMKM hanya dibuat dan diedit oleh pemilik UMKM
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/umkms/**"
                ).hasRole("UMKM")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/umkms/**"
                ).hasRole("UMKM")

                // Kategori hanya dikelola Admin
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/categories/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/categories/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/categories/**"
                ).hasRole("ADMIN")

                // Produk hanya dikelola pemilik UMKM
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/products/**"
                ).hasRole("UMKM")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/products/**"
                ).hasRole("UMKM")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/products/**"
                ).hasRole("UMKM")

                // Laporan boleh dilihat UMKM, Admin, dan DPRD
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/monthly-reports/**"
                ).hasAnyRole("UMKM", "ADMIN", "DPRD")

                // Laporan hanya boleh dikelola pemilik UMKM
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/monthly-reports/**"
                ).hasRole("UMKM")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/monthly-reports/**"
                ).hasRole("UMKM")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/monthly-reports/**"
                ).hasRole("UMKM")

                // Endpoint lainnya wajib login
                .anyRequest().authenticated()
            )

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(
                        (request, response, error) ->
                                response.sendError(
                                        401,
                                        "Silakan login terlebih dahulu."
                                )
                )
                .accessDeniedHandler(
                        (request, response, error) ->
                                response.sendError(
                                        403,
                                        "Anda tidak memiliki hak akses."
                                )
                )
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}