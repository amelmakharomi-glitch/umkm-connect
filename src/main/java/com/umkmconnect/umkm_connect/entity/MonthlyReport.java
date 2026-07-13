package com.umkmconnect.umkm_connect.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "monthly_reports",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"umkm_id", "bulan", "tahun"})
    }
)
public class MonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "umkm_id", nullable = false)
    private Umkm umkm;

    @Column(nullable = false)
    private Integer bulan;

    @Column(nullable = false)
    private Integer tahun;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal omzetKotor = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal omzetBersih = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer jumlahTransaksi = 0;

    private String produkTerlaris;

    @Column(nullable = false)
    private Integer jumlahTenagaKerja = 0;

    @Column(columnDefinition = "TEXT")
    private String kendala;

    @Column(columnDefinition = "TEXT")
    private String catatan;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MonthlyReport() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Umkm getUmkm() {
        return umkm;
    }

    public void setUmkm(Umkm umkm) {
        this.umkm = umkm;
    }

    public Integer getBulan() {
        return bulan;
    }

    public void setBulan(Integer bulan) {
        this.bulan = bulan;
    }

    public Integer getTahun() {
        return tahun;
    }

    public void setTahun(Integer tahun) {
        this.tahun = tahun;
    }

    public BigDecimal getOmzetKotor() {
        return omzetKotor;
    }

    public void setOmzetKotor(BigDecimal omzetKotor) {
        this.omzetKotor = omzetKotor;
    }

    public BigDecimal getOmzetBersih() {
        return omzetBersih;
    }

    public void setOmzetBersih(BigDecimal omzetBersih) {
        this.omzetBersih = omzetBersih;
    }

    public Integer getJumlahTransaksi() {
        return jumlahTransaksi;
    }

    public void setJumlahTransaksi(Integer jumlahTransaksi) {
        this.jumlahTransaksi = jumlahTransaksi;
    }

    public String getProdukTerlaris() {
        return produkTerlaris;
    }

    public void setProdukTerlaris(String produkTerlaris) {
        this.produkTerlaris = produkTerlaris;
    }

    public Integer getJumlahTenagaKerja() {
        return jumlahTenagaKerja;
    }

    public void setJumlahTenagaKerja(Integer jumlahTenagaKerja) {
        this.jumlahTenagaKerja = jumlahTenagaKerja;
    }

    public String getKendala() {
        return kendala;
    }

    public void setKendala(String kendala) {
        this.kendala = kendala;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}