package com.umkmconnect.umkm_connect.controller;

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
import com.umkmconnect.umkm_connect.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Melihat seluruh kategori
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // Melihat detail kategori berdasarkan ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Menambah kategori baru
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        String namaKategori = category.getNamaKategori();

        if (namaKategori == null || namaKategori.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Nama kategori wajib diisi.")
            );
        }

        namaKategori = namaKategori.trim();

        if (categoryService.namaKategoriSudahAda(namaKategori)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("message", "Nama kategori sudah terdaftar.")
            );
        }

        category.setNamaKategori(namaKategori);
        Category savedCategory = categoryService.saveCategory(category);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    // Mengubah kategori
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody Category categoryRequest
    ) {
        return categoryService.getCategoryById(id)
                .map(category -> {
                    String namaKategori = categoryRequest.getNamaKategori();

                    if (namaKategori == null || namaKategori.trim().isEmpty()) {
                        return ResponseEntity.badRequest().body(
                                Map.of("message", "Nama kategori wajib diisi.")
                        );
                    }

                    namaKategori = namaKategori.trim();

                    boolean namaDipakaiKategoriLain =
                            categoryService.namaKategoriSudahAda(namaKategori)
                                    && !category.getNamaKategori()
                                    .equalsIgnoreCase(namaKategori);

                    if (namaDipakaiKategoriLain) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                Map.of("message", "Nama kategori sudah terdaftar.")
                        );
                    }

                    category.setNamaKategori(namaKategori);
                    return ResponseEntity.ok(
                            categoryService.saveCategory(category)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Menghapus kategori
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        if (categoryService.getCategoryById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        categoryService.deleteCategory(id);

        return ResponseEntity.ok(
                Map.of("message", "Kategori berhasil dihapus.")
        );
    }
}