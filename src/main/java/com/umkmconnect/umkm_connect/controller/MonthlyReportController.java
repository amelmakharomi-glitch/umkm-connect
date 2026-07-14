package com.umkmconnect.umkm_connect.controller;

import java.math.BigDecimal;
import java.time.Year;
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

import com.umkmconnect.umkm_connect.entity.MonthlyReport;
import com.umkmconnect.umkm_connect.entity.Umkm;
import com.umkmconnect.umkm_connect.service.MonthlyReportService;
import com.umkmconnect.umkm_connect.service.UmkmService;

@RestController
@RequestMapping("/api/monthly-reports")
public class MonthlyReportController {

    private final MonthlyReportService monthlyReportService;
    private final UmkmService umkmService;

    public MonthlyReportController(
            MonthlyReportService monthlyReportService,
            UmkmService umkmService
    ) {
        this.monthlyReportService = monthlyReportService;
        this.umkmService = umkmService;
    }

    @GetMapping
    public List<MonthlyReport> getAllReports() {
        return monthlyReportService.getAllReports();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonthlyReport> getReportById(
            @PathVariable Long id
    ) {
        return monthlyReportService.getReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/umkm/{umkmId}")
    public List<MonthlyReport> getReportsByUmkmId(
            @PathVariable Long umkmId
    ) {
        return monthlyReportService.getReportsByUmkmId(umkmId);
    }

    @GetMapping("/umkm/{umkmId}/{tahun}/{bulan}")
    public ResponseEntity<MonthlyReport> getReportByPeriode(
            @PathVariable Long umkmId,
            @PathVariable Integer tahun,
            @PathVariable Integer bulan
    ) {
        return monthlyReportService
                .getReportByPeriode(umkmId, bulan, tahun)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestBody MonthlyReport reportRequest
    ) {
        String validationError = validateReport(reportRequest);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", validationError)
            );
        }

        Long umkmId = reportRequest.getUmkm().getId();

        Umkm umkm = umkmService
                .getUmkmById(umkmId)
                .orElse(null);

        if (umkm == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "UMKM tidak ditemukan.")
            );
        }

        boolean laporanSudahAda = monthlyReportService.laporanSudahAda(
                umkmId,
                reportRequest.getBulan(),
                reportRequest.getTahun()
        );

        if (laporanSudahAda) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "message",
                            "Laporan untuk bulan dan tahun tersebut sudah ada."
                    )
            );
        }

        reportRequest.setUmkm(umkm);
        setDefaultValues(reportRequest);

        MonthlyReport savedReport =
                monthlyReportService.saveReport(reportRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedReport);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long id,
            @RequestBody MonthlyReport reportRequest
    ) {
        MonthlyReport existingReport = monthlyReportService
                .getReportById(id)
                .orElse(null);

        if (existingReport == null) {
            return ResponseEntity.notFound().build();
        }

        String validationError = validateReport(reportRequest);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", validationError)
            );
        }

        Long umkmId = reportRequest.getUmkm().getId();

        Umkm umkm = umkmService
                .getUmkmById(umkmId)
                .orElse(null);

        if (umkm == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "UMKM tidak ditemukan.")
            );
        }

        MonthlyReport reportPadaPeriodeSama = monthlyReportService
                .getReportByPeriode(
                        umkmId,
                        reportRequest.getBulan(),
                        reportRequest.getTahun()
                )
                .orElse(null);

        if (reportPadaPeriodeSama != null
                && !reportPadaPeriodeSama.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "message",
                            "Laporan untuk bulan dan tahun tersebut sudah ada."
                    )
            );
        }

        existingReport.setUmkm(umkm);
        existingReport.setBulan(reportRequest.getBulan());
        existingReport.setTahun(reportRequest.getTahun());
        existingReport.setOmzetKotor(
                defaultBigDecimal(reportRequest.getOmzetKotor())
        );
        existingReport.setOmzetBersih(
                defaultBigDecimal(reportRequest.getOmzetBersih())
        );
        existingReport.setJumlahTransaksi(
                defaultInteger(reportRequest.getJumlahTransaksi())
        );
        existingReport.setProdukTerlaris(
                reportRequest.getProdukTerlaris()
        );
        existingReport.setJumlahTenagaKerja(
                defaultInteger(reportRequest.getJumlahTenagaKerja())
        );
        existingReport.setKendala(reportRequest.getKendala());
        existingReport.setCatatan(reportRequest.getCatatan());

        MonthlyReport updatedReport =
                monthlyReportService.saveReport(existingReport);

        return ResponseEntity.ok(updatedReport);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(
            @PathVariable Long id
    ) {
        if (monthlyReportService.getReportById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        monthlyReportService.deleteReport(id);

        return ResponseEntity.ok(
                Map.of("message", "Laporan bulanan berhasil dihapus.")
        );
    }

    private String validateReport(MonthlyReport report) {
        if (report.getUmkm() == null
                || report.getUmkm().getId() == null) {
            return "UMKM wajib dipilih.";
        }

        if (report.getBulan() == null
                || report.getBulan() < 1
                || report.getBulan() > 12) {
            return "Bulan harus berupa angka 1 sampai 12.";
        }

        int tahunSekarang = Year.now().getValue();

        if (report.getTahun() == null
                || report.getTahun() < 2000
                || report.getTahun() > tahunSekarang + 1) {
            return "Tahun laporan tidak valid.";
        }

        if (isNegative(report.getOmzetKotor())) {
            return "Omzet kotor tidak boleh negatif.";
        }

        if (isNegative(report.getOmzetBersih())) {
            return "Omzet bersih tidak boleh negatif.";
        }

        Integer jumlahTransaksi = report.getJumlahTransaksi();

        if (jumlahTransaksi != null && jumlahTransaksi < 0) {
            return "Jumlah transaksi tidak boleh negatif.";
        }

        Integer jumlahTenagaKerja = report.getJumlahTenagaKerja();

        if (jumlahTenagaKerja != null && jumlahTenagaKerja < 0) {
            return "Jumlah tenaga kerja tidak boleh negatif.";
        }

        return null;
    }

    private void setDefaultValues(MonthlyReport report) {
        report.setOmzetKotor(
                defaultBigDecimal(report.getOmzetKotor())
        );
        report.setOmzetBersih(
                defaultBigDecimal(report.getOmzetBersih())
        );
        report.setJumlahTransaksi(
                defaultInteger(report.getJumlahTransaksi())
        );
        report.setJumlahTenagaKerja(
                defaultInteger(report.getJumlahTenagaKerja())
        );
    }

    private BigDecimal defaultBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer defaultInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private boolean isNegative(BigDecimal value) {
        return value != null
                && value.compareTo(BigDecimal.ZERO) < 0;
    }
}