package com.umkmconnect.umkm_connect.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.umkmconnect.umkm_connect.entity.Umkm;
import com.umkmconnect.umkm_connect.repository.UmkmRepository;

@Service
public class UmkmService {

    private final UmkmRepository umkmRepository;

    public UmkmService(UmkmRepository umkmRepository) {
        this.umkmRepository = umkmRepository;
    }

    public List<Umkm> getAllUmkm() {
        return umkmRepository.findAll();
    }

    public Optional<Umkm> getUmkmById(Long id) {
        return umkmRepository.findById(id);
    }

    public Optional<Umkm> getUmkmByUserId(Long userId) {
        return umkmRepository.findByUserId(userId);
    }

    public Optional<Umkm> getUmkmBySlug(String slug) {
        return umkmRepository.findBySlug(slug);
    }

    public Umkm saveUmkm(Umkm umkm) {
        return umkmRepository.save(umkm);
    }

    public boolean userSudahPunyaUmkm(Long userId) {
        return umkmRepository.existsByUserId(userId);
    }

    public boolean slugSudahDipakai(String slug) {
        return umkmRepository.existsBySlug(slug);
    }

    public String generateUniqueSlug(String namaUsaha) {
        String baseSlug = namaUsaha
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        if (baseSlug.isEmpty()) {
            baseSlug = "umkm";
        }

        String slug = baseSlug;
        int nomor = 2;

        while (slugSudahDipakai(slug)) {
            slug = baseSlug + "-" + nomor;
            nomor++;
        }

        return slug;
    }

    public void deleteUmkm(Long id) {
        umkmRepository.deleteById(id);
    }
}