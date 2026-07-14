package com.umkmconnect.umkm_connect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umkmconnect.umkm_connect.entity.Umkm;

public interface UmkmRepository extends JpaRepository<Umkm, Long> {

    Optional<Umkm> findByUserId(Long userId);

    Optional<Umkm> findBySlug(String slug);

    boolean existsByUserId(Long userId);

    boolean existsBySlug(String slug);
}