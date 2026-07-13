package com.umkmconnect.umkm_connect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umkmconnect.umkm_connect.entity.Umkm;

public interface UmkmRepository extends JpaRepository<Umkm, Long> {

    Optional<Umkm> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}