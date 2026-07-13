package com.umkmconnect.umkm_connect.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.umkmconnect.umkm_connect.entity.Category;
import com.umkmconnect.umkm_connect.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByNama(String namaKategori) {
        return categoryRepository.findByNamaKategori(namaKategori);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public boolean namaKategoriSudahAda(String namaKategori) {
        return categoryRepository.existsByNamaKategori(namaKategori);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}