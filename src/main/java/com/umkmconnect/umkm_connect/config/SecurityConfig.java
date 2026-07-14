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

                // Halaman umum dan autentikasi
                .requestMatchers(
                        "/",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/users/register"
                ).permitAll()

                // Data publik yang dapat dilihat masyarakat
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/umkms/**",
                        "/api/products/**",
                        "/api/categories/**"
                ).permitAll()

                // Data akun hanya untuk Admin
                .requestMatchers("/api/users/**")
                .hasRole("ADMIN")

                // Verifikasi UMKM khusus Admin
                .requestMatchers(
                        HttpMethod.PATCH,
                        "/api/umkms/*/status"
                ).hasRole("ADMIN")

                // Menghapus UMKM khusus Admin
                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/umkms/**"
                ).hasRole("ADMIN")

                // Membuat dan mengubah profil UMKM
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/umkms/**"
                ).hasAnyRole("UMKM", "ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/umkms/**"
                ).hasAnyRole("UMKM", "ADMIN")

                // Pengelolaan kategori khusus Admin
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

                // Pengelolaan produk oleh UMKM dan Admin
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/products/**"
                ).hasAnyRole("UMKM", "ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/products/**"
                ).hasAnyRole("UMKM", "ADMIN")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/products/**"
                ).hasAnyRole("UMKM", "ADMIN")

                // Laporan dapat dilihat pihak internal
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/monthly-reports/**"
                ).hasAnyRole("UMKM", "ADMIN", "DPRD")

                // Laporan hanya dikelola UMKM dan Admin
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/monthly-reports/**"
                ).hasAnyRole("UMKM", "ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/monthly-reports/**"
                ).hasAnyRole("UMKM", "ADMIN")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/monthly-reports/**"
                ).hasAnyRole("UMKM", "ADMIN")

                // Endpoint lain wajib login
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