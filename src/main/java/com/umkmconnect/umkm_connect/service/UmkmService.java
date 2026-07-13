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

    public Umkm saveUmkm(Umkm umkm) {
        return umkmRepository.save(umkm);
    }

    public boolean userSudahPunyaUmkm(Long userId) {
        return umkmRepository.existsByUserId(userId);
    }

    public void deleteUmkm(Long id) {
        umkmRepository.deleteById(id);
    }
}