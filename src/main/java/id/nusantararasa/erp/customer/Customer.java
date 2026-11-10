package id.nusantararasa.erp.customer;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kode;
    private String nama;
    private String kota;

    @Enumerated(EnumType.STRING)
    private Segmen segmen;

    private int terminHari;
    private BigDecimal limitKredit;

    public Long getId() {
        return id;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public Segmen getSegmen() {
        return segmen;
    }

    public void setSegmen(Segmen segmen) {
        this.segmen = segmen;
    }

    public int getTerminHari() {
        return terminHari;
    }

    public void setTerminHari(int terminHari) {
        this.terminHari = terminHari;
    }

    public BigDecimal getLimitKredit() {
        return limitKredit;
    }

    public void setLimitKredit(BigDecimal limitKredit) {
        this.limitKredit = limitKredit;
    }
}
