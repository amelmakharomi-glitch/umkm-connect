package com.umkmconnect.umkm_connect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umkmconnect.umkm_connect.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByUmkmId(Long umkmId);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByUmkmIdAndStatusTersediaTrue(Long umkmId);
}