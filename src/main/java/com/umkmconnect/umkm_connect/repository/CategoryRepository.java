package com.umkmconnect.umkm_connect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umkmconnect.umkm_connect.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNamaKategori(String namaKategori);

    boolean existsByNamaKategori(String namaKategori);
}