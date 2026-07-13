package com.umkmconnect.umkm_connect.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.umkmconnect.umkm_connect.entity.Product;
import com.umkmconnect.umkm_connect.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByUmkmId(Long umkmId) {
        return productRepository.findByUmkmId(umkmId);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getAvailableProductsByUmkmId(Long umkmId) {
        return productRepository.findByUmkmIdAndStatusTersediaTrue(umkmId);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}