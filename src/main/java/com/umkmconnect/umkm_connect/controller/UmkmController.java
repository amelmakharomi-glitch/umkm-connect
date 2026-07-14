package com.umkmconnect.umkm_connect.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umkmconnect.umkm_connect.entity.Role;
import com.umkmconnect.umkm_connect.entity.StatusVerifikasi;
import com.umkmconnect.umkm_connect.entity.Umkm;
import com.umkmconnect.umkm_connect.entity.User;
import com.umkmconnect.umkm_connect.service.OwnershipService;
import com.umkmconnect.umkm_connect.service.UmkmService;
import com.umkmconnect.umkm_connect.service.UserService;

@RestController
@RequestMapping("/api/umkms")
public class UmkmController {

    private final UmkmService umkmService;
    private final UserService userService;
    private final OwnershipService ownershipService;

    public UmkmController(
            UmkmService umkmService,
            UserService userService,
            OwnershipService ownershipService
    ) {
        this.umkmService = umkmService;
        this.userService = userService;
        this.ownershipService = ownershipService;
    }

    @GetMapping
    public List<Umkm> getAllUmkm() {
        return umkmService.getAllUmkm();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Umkm> getUmkmById(
            @PathVariable Long id
    ) {
        return umkmService.getUmkmById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Umkm> getUmkmByUserId(
            @PathVariable Long userId
    ) {
        return umkmService.getUmkmByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUmkm(
            @RequestBody Umkm umkmRequest,
            Authentication authentication
    ) {
        String validationError = validateUmkm(umkmRequest);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", validationError)
            );
        }

        User currentUser =
                ownershipService.getCurrentUser(authentication);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Silakan login terlebih dahulu.")
            );
        }

        Long userId = umkmRequest.getUser().getId();

        User pemilik = userService
                .getUserById(userId)
                .orElse(null);

        if (pemilik == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Akun pengguna tidak ditemukan.")
            );
        }

        if (pemilik.getRole() != Role.UMKM) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Profil UMKM hanya dapat dimiliki akun dengan role UMKM."
                    )
            );
        }

        if (currentUser.getRole() == Role.UMKM
                && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of(
                            "message",
                            "Anda hanya dapat membuat profil UMKM untuk akun sendiri."
                    )
            );
        }

        if (currentUser.getRole() != Role.UMKM
                && currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("message", "Anda tidak memiliki hak akses.")
            );
        }

        if (umkmService.userSudahPunyaUmkm(userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "message",
                            "Akun tersebut sudah memiliki profil UMKM."
                    )
            );
        }

        umkmRequest.setUser(pemilik);
        umkmRequest.setNamaUsaha(
                umkmRequest.getNamaUsaha().trim()
        );
        umkmRequest.setAlamat(
                umkmRequest.getAlamat().trim()
        );
        umkmRequest.setNoWhatsapp(
                umkmRequest.getNoWhatsapp().trim()
        );

        umkmRequest.setStatusVerifikasi(
                StatusVerifikasi.PENDING
        );

        String slug = umkmService.generateUniqueSlug(
                umkmRequest.getNamaUsaha()
        );

        umkmRequest.setSlug(slug);

        Umkm savedUmkm = umkmService.saveUmkm(umkmRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUmkm);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUmkm(
            @PathVariable Long id,
            @RequestBody Umkm umkmRequest,
            Authentication authentication
    ) {
        Umkm existingUmkm = umkmService
                .getUmkmById(id)
                .orElse(null);

        if (existingUmkm == null) {
            return ResponseEntity.notFound().build();
        }

        if (!ownershipService.canManageUmkm(authentication, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of(
                            "message",
                            "Anda tidak boleh mengubah profil UMKM ini."
                    )
            );
        }

        String validationError =
                validateProfilUmkm(umkmRequest);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", validationError)
            );
        }

        existingUmkm.setNamaUsaha(
                umkmRequest.getNamaUsaha().trim()
        );
        existingUmkm.setDeskripsi(
                umkmRequest.getDeskripsi()
        );
        existingUmkm.setAlamat(
                umkmRequest.getAlamat().trim()
        );
        existingUmkm.setNoWhatsapp(
                umkmRequest.getNoWhatsapp().trim()
        );
        existingUmkm.setJamOperasional(
                umkmRequest.getJamOperasional()
        );
        existingUmkm.setLogo(
                umkmRequest.getLogo()
        );

        Umkm updatedUmkm =
                umkmService.saveUmkm(existingUmkm);

        return ResponseEntity.ok(updatedUmkm);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatusVerifikasi(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        Umkm existingUmkm = umkmService
                .getUmkmById(id)
                .orElse(null);

        if (existingUmkm == null) {
            return ResponseEntity.notFound().build();
        }

        String status = request.get("statusVerifikasi");

        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Status verifikasi wajib diisi."
                    )
            );
        }

        try {
            StatusVerifikasi statusVerifikasi =
                    StatusVerifikasi.valueOf(
                            status.trim().toUpperCase()
                    );

            existingUmkm.setStatusVerifikasi(
                    statusVerifikasi
            );

            Umkm updatedUmkm =
                    umkmService.saveUmkm(existingUmkm);

            return ResponseEntity.ok(updatedUmkm);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Status harus PENDING, AKTIF, atau NONAKTIF."
                    )
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUmkm(
            @PathVariable Long id
    ) {
        if (umkmService.getUmkmById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        umkmService.deleteUmkm(id);

        return ResponseEntity.ok(
                Map.of("message", "Data UMKM berhasil dihapus.")
        );
    }

    private String validateUmkm(Umkm umkm) {
        if (umkm.getUser() == null
                || umkm.getUser().getId() == null) {
            return "Akun pemilik UMKM wajib dipilih.";
        }

        return validateProfilUmkm(umkm);
    }

    private String validateProfilUmkm(Umkm umkm) {
        if (umkm.getNamaUsaha() == null
                || umkm.getNamaUsaha().trim().isEmpty()) {
            return "Nama usaha wajib diisi.";
        }

        if (umkm.getAlamat() == null
                || umkm.getAlamat().trim().isEmpty()) {
            return "Alamat usaha wajib diisi.";
        }

        if (umkm.getNoWhatsapp() == null
                || umkm.getNoWhatsapp().trim().isEmpty()) {
            return "Nomor WhatsApp wajib diisi.";
        }

        if (!umkm.getNoWhatsapp()
                .trim()
                .matches("[0-9+\\- ]+")) {
            return "Format nomor WhatsApp tidak valid.";
        }

        return null;
    }
}