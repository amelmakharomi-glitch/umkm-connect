package com.umkmconnect.umkm_connect.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.WriterException;
import com.umkmconnect.umkm_connect.entity.Product;
import com.umkmconnect.umkm_connect.entity.StatusVerifikasi;
import com.umkmconnect.umkm_connect.entity.Umkm;
import com.umkmconnect.umkm_connect.service.ProductService;
import com.umkmconnect.umkm_connect.service.QrCodeService;
import com.umkmconnect.umkm_connect.service.UmkmService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/u")
public class PublicPageController {

    private final UmkmService umkmService;
    private final ProductService productService;
    private final QrCodeService qrCodeService;

    public PublicPageController(
            UmkmService umkmService,
            ProductService productService,
            QrCodeService qrCodeService
    ) {
        this.umkmService = umkmService;
        this.productService = productService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/{slug}")
    public String showPublicUmkmPage(
            @PathVariable String slug,
            Model model
    ) {
        Umkm umkm = umkmService
                .getUmkmBySlug(slug)
                .orElse(null);

        if (umkm == null
                || umkm.getStatusVerifikasi()
                != StatusVerifikasi.AKTIF) {
            return "public-umkm-not-found";
        }

        List<Product> products = productService
                .getAvailableProductsByUmkmId(umkm.getId());

        model.addAttribute("umkm", umkm);
        model.addAttribute("products", products);

        return "public-umkm";
    }

    @GetMapping(
            value = "/{slug}/qr.png",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    @ResponseBody
    public ResponseEntity<byte[]> getUmkmQrCode(
            @PathVariable String slug,
            HttpServletRequest request
    ) {
        Umkm umkm = umkmService
                .getUmkmBySlug(slug)
                .orElse(null);

        if (umkm == null) {
            return ResponseEntity.notFound().build();
        }

        String baseUrl =
                request.getScheme()
                        + "://"
                        + request.getServerName()
                        + ":"
                        + request.getServerPort();

        String publicUrl =
                baseUrl + "/u/" + umkm.getSlug();

        try {
            byte[] qrCode = qrCodeService.generateQrCode(
                    publicUrl,
                    400,
                    400
            );

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"qr-"
                                    + umkm.getSlug()
                                    + ".png\""
                    )
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCode);

        } catch (WriterException | IOException exception) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
}