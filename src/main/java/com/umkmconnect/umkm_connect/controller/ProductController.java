package com.umkmconnect.umkm_connect.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umkmconnect.umkm_connect.entity.Product;
import com.umkmconnect.umkm_connect.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/umkm/{umkmId}")
    public List<Product> getProductsByUmkmId(@PathVariable Long umkmId) {
        return productService.getProductsByUmkmId(umkmId);
    }

    @GetMapping("/umkm/{umkmId}/available")
    public List<Product> getAvailableProductsByUmkmId(
            @PathVariable Long umkmId
    ) {
        return productService.getAvailableProductsByUmkmId(umkmId);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategoryId(
            @PathVariable Long categoryId
    ) {
        return productService.getProductsByCategoryId(categoryId);
    }
}