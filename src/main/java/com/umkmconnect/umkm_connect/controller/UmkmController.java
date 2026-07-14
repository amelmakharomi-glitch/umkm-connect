package com.umkmconnect.umkm_connect.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.umkmconnect.umkm_connect.service.UmkmService;
import com.umkmconnect.umkm_connect.service.UserService;

@RestController
@RequestMapping("/api/umkms")
public class UmkmController {

    private final UmkmService umkmService;
    private final UserService userService;

    public UmkmController(
            UmkmService umkmService,
            UserService userService
    ) {
        this.umkmService = umkmService;
        this.userService = userService;
    }

    // Melihat seluruh UMKM
    @GetMapping
    public List<Umkm> getAllUmkm() {
        return umkmService.getAllUmkm();
    }

    // Melihat detail UMKM berdasarkan ID
    @GetMapping("/{id}")
    public ResponseEntity<Umkm> getUmkmById(
            @PathVariable Long id
    ) {
        return umkmService.getUmkmById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Melihat UMKM berdasarkan akun pemilik
    @GetMapping("/user/{userId}")
    public ResponseEntity<Umkm> getUmkmByUserId(
            @PathVariable Long userId
    ) {
        return umkmService.getUmkmByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Membuat data profil UMKM
    @PostMapping
    public ResponseEntity<?> createUmkm(
            @RequestBody Umkm umkmRequest
    ) {
        String validationError = validateUmkm(umkmRequest);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", validationError)
            );
        }

        Long userId = umkmRequest.getUser().getId();

        User user = userService
                .getUserById(userId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Akun pengguna tidak ditemukan.")
            );
        }

        if (user.getRole() != Role.UMKM) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Profil UMKM hanya dapat dimiliki akun dengan role UMKM."
                    )
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

        umkmRequest.setUser(user);
        umkmRequest.setNamaUsaha(
                umkmRequest.getNamaUsaha().trim()
        );
        umkmRequest.setAlamat(
                umkmRequest.getAlamat().trim()
        );
        umkmRequest.setNoWhatsapp(
                umkmRequest.getNoWhatsapp().trim()
        );

        if (umkmRequest.getStatusVerifikasi() == null) {
            umkmRequest.setStatusVerifikasi(
                    StatusVerifikasi.PENDING
            );
        }

        Umkm savedUmkm = umkmService.saveUmkm(umkmRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUmkm);
    }

    // Pemilik UMKM memperbarui profil usahanya
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUmkm(
            @PathVariable Long id,
            @RequestBody Umkm umkmRequest
    ) {
        Umkm existingUmkm = umkmService
                .getUmkmById(id)
                .orElse(null);

        if (existingUmkm == null) {
            return ResponseEntity.notFound().build();
        }

        String validationError = validateProfilUmkm(umkmRequest);

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

    // Admin mengubah status verifikasi UMKM
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

            existingUmkm.setStatusVerifikasi(statusVerifikasi);

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

    // Menghapus data UMKM
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

        if (!umkm.getNoWhatsapp().trim().matches("[0-9+\\- ]+")) {
            return "Format nomor WhatsApp tidak valid.";
        }

        return null;
    }
}