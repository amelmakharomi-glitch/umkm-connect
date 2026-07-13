package com.umkmconnect.umkm_connect.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umkmconnect.umkm_connect.entity.Umkm;
import com.umkmconnect.umkm_connect.service.UmkmService;

@RestController
@RequestMapping("/api/umkms")
public class UmkmController {

    private final UmkmService umkmService;

    public UmkmController(UmkmService umkmService) {
        this.umkmService = umkmService;
    }

    @GetMapping
    public List<Umkm> getAllUmkm() {
        return umkmService.getAllUmkm();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Umkm> getUmkmById(@PathVariable Long id) {
        return umkmService.getUmkmById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Umkm> getUmkmByUserId(@PathVariable Long userId) {
        return umkmService.getUmkmByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}