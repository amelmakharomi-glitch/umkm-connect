package com.umkmconnect.umkm_connect.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.umkmconnect.umkm_connect.entity.MonthlyReport;
import com.umkmconnect.umkm_connect.repository.MonthlyReportRepository;

@Service
public class MonthlyReportService {

    private final MonthlyReportRepository monthlyReportRepository;

    public MonthlyReportService(MonthlyReportRepository monthlyReportRepository) {
        this.monthlyReportRepository = monthlyReportRepository;
    }

    public List<MonthlyReport> getAllReports() {
        return monthlyReportRepository.findAll();
    }

    public Optional<MonthlyReport> getReportById(Long id) {
        return monthlyReportRepository.findById(id);
    }

    public List<MonthlyReport> getReportsByUmkmId(Long umkmId) {
        return monthlyReportRepository
                .findByUmkmIdOrderByTahunDescBulanDesc(umkmId);
    }

    public Optional<MonthlyReport> getReportByPeriode(
            Long umkmId,
            Integer bulan,
            Integer tahun
    ) {
        return monthlyReportRepository
                .findByUmkmIdAndBulanAndTahun(umkmId, bulan, tahun);
    }

    public boolean laporanSudahAda(
            Long umkmId,
            Integer bulan,
            Integer tahun
    ) {
        return monthlyReportRepository
                .existsByUmkmIdAndBulanAndTahun(umkmId, bulan, tahun);
    }

    public MonthlyReport saveReport(MonthlyReport report) {
        return monthlyReportRepository.save(report);
    }

    public void deleteReport(Long id) {
        monthlyReportRepository.deleteById(id);
    }
}