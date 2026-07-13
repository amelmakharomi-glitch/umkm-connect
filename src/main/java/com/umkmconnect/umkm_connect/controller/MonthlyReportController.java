package com.umkmconnect.umkm_connect.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umkmconnect.umkm_connect.entity.MonthlyReport;
import com.umkmconnect.umkm_connect.service.MonthlyReportService;

@RestController
@RequestMapping("/api/monthly-reports")
public class MonthlyReportController {

    private final MonthlyReportService monthlyReportService;

    public MonthlyReportController(MonthlyReportService monthlyReportService) {
        this.monthlyReportService = monthlyReportService;
    }

    @GetMapping
    public List<MonthlyReport> getAllReports() {
        return monthlyReportService.getAllReports();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonthlyReport> getReportById(@PathVariable Long id) {
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
}