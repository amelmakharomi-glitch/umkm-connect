package com.umkmconnect.umkm_connect.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umkmconnect.umkm_connect.entity.Category;
import com.umkmconnect.umkm_connect.entity.Product;
import com.umkmconnect.umkm_connect.entity.Umkm;
import com.umkmconnect.umkm_connect.service.CategoryService;
import com.umkmconnect.umkm_connect.service.ProductService;
import com.umkmconnect.umkm_connect.service.UmkmService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UmkmService umkmService;
    private final CategoryService categoryService;

    public ProductController(
            ProductService productService,
            UmkmService umkmService,
            CategoryService categoryService
    ) {
        this.productService = productService;
        this.umkmService = umkmService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id
    ) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/umkm/{umkmId}")
    public List<Product> getProductsByUmkmId(
            @PathVariable Long umkmId
    ) {
        return productService.getProductsByUmkmId(umkmId);
    }

    @GetMapping("/umkm/{umkmId}/available")
    public List<Product> getAvailableProductsByUmkmId(
            @PathVariable Long umkmId
    ) {
        return productService
                .getAvailableProductsByUmkmId(umkmId);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategoryId(
            @PathVariable Long categoryId
    ) {
        return productService
                .getProductsByCategoryId(categoryId);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(
            @RequestBody Product productRequest
    ) {
        String validationError = validateProduct(productRequest);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", validationError)
            );
        }

        Long umkmId = productRequest.getUmkm().getId();
        Long categoryId = productRequest.getCategory().getId();

        Umkm umkm = umkmService
                .getUmkmById(umkmId)
                .orElse(null);

        if (umkm == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "UMKM tidak ditemukan.")
            );
        }

        Category category = categoryService
                .getCategoryById(categoryId)
                .orElse(null);

        if (category == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Kategori tidak ditemukan.")
            );
        }

        productRequest.setUmkm(umkm);
        productRequest.setCategory(category);
        productRequest.setNamaProduk(
                productRequest.getNamaProduk().trim()
        );

        if (productRequest.getStok() == null) {
            productRequest.setStok(0);
        }

        if (productRequest.getStatusTersedia() == null) {
            productRequest.setStatusTersedia(true);
        }

        Product savedProduct =
                productService.saveProduct(productRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product productRequest
    ) {
        Product existingProduct = productService
                .getProductById(id)
                .orElse(null);

        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        String validationError = validateProduct(productRequest);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", validationError)
            );
        }

        Long umkmId = productRequest.getUmkm().getId();
        Long categoryId = productRequest.getCategory().getId();

        Umkm umkm = umkmService
                .getUmkmById(umkmId)
                .orElse(null);

        if (umkm == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "UMKM tidak ditemukan.")
            );
        }

        Category category = categoryService
                .getCategoryById(categoryId)
                .orElse(null);

        if (category == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Kategori tidak ditemukan.")
            );
        }

        existingProduct.setUmkm(umkm);
        existingProduct.setCategory(category);
        existingProduct.setNamaProduk(
                productRequest.getNamaProduk().trim()
        );
        existingProduct.setDeskripsi(
                productRequest.getDeskripsi()
        );
        existingProduct.setHarga(
                productRequest.getHarga()
        );
        existingProduct.setFoto(
                productRequest.getFoto()
        );

        Integer stok = productRequest.getStok();

        existingProduct.setStok(
                stok == null ? 0 : stok
        );

        Boolean statusTersedia =
                productRequest.getStatusTersedia();

        existingProduct.setStatusTersedia(
                statusTersedia == null
                        ? true
                        : statusTersedia
        );

        Product updatedProduct =
                productService.saveProduct(existingProduct);

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long id
    ) {
        if (productService.getProductById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                Map.of("message", "Produk berhasil dihapus.")
        );
    }

    private String validateProduct(Product product) {
        if (product.getNamaProduk() == null
                || product.getNamaProduk().trim().isEmpty()) {
            return "Nama produk wajib diisi.";
        }

        if (product.getHarga() == null
                || product.getHarga()
                .compareTo(BigDecimal.ZERO) < 0) {
            return "Harga tidak boleh kosong atau negatif.";
        }

        Integer stok = product.getStok();

        if (stok != null && stok < 0) {
            return "Stok tidak boleh negatif.";
        }

        if (product.getUmkm() == null
                || product.getUmkm().getId() == null) {
            return "UMKM wajib dipilih.";
        }

        if (product.getCategory() == null
                || product.getCategory().getId() == null) {
            return "Kategori wajib dipilih.";
        }

        return null;
    }
}