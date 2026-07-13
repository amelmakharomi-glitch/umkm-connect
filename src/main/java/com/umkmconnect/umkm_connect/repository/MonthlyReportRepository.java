package com.umkmconnect.umkm_connect.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umkmconnect.umkm_connect.entity.MonthlyReport;

public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long> {

    List<MonthlyReport> findByUmkmIdOrderByTahunDescBulanDesc(Long umkmId);

    Optional<MonthlyReport> findByUmkmIdAndBulanAndTahun(
        Long umkmId,
        Integer bulan,
        Integer tahun
    );

    boolean existsByUmkmIdAndBulanAndTahun(
        Long umkmId,
        Integer bulan,
        Integer tahun
    );
}