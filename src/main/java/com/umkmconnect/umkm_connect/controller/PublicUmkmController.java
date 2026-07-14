package com.umkmconnect.umkm_connect.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umkmconnect.umkm_connect.entity.Product;
import com.umkmconnect.umkm_connect.entity.StatusVerifikasi;
import com.umkmconnect.umkm_connect.entity.Umkm;
import com.umkmconnect.umkm_connect.service.ProductService;
import com.umkmconnect.umkm_connect.service.UmkmService;

@RestController
@RequestMapping("/api/public/umkm")
public class PublicUmkmController {

    private final UmkmService umkmService;
    private final ProductService productService;

    public PublicUmkmController(
            UmkmService umkmService,
            ProductService productService
    ) {
        this.umkmService = umkmService;
        this.productService = productService;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getPublicUmkm(
            @PathVariable String slug
    ) {
        Umkm umkm = umkmService
                .getUmkmBySlug(slug)
                .orElse(null);

        if (umkm == null
                || umkm.getStatusVerifikasi()
                != StatusVerifikasi.AKTIF) {
            return ResponseEntity.notFound().build();
        }

        List<Product> products = productService
                .getAvailableProductsByUmkmId(umkm.getId());

        return ResponseEntity.ok(
                Map.of(
                        "id", umkm.getId(),
                        "slug", umkm.getSlug(),
                        "namaUsaha", umkm.getNamaUsaha(),
                        "deskripsi",
                        umkm.getDeskripsi() == null
                                ? ""
                                : umkm.getDeskripsi(),
                        "alamat", umkm.getAlamat(),
                        "noWhatsapp", umkm.getNoWhatsapp(),
                        "jamOperasional",
                        umkm.getJamOperasional() == null
                                ? ""
                                : umkm.getJamOperasional(),
                        "logo",
                        umkm.getLogo() == null
                                ? ""
                                : umkm.getLogo(),
                        "products", products
                )
        );
    }
}