package id.nusantararasa.erp.produk;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "produk")
public class Produk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kode;
    private String nama;
    private String satuan;
    private BigDecimal hargaJual;
    private boolean aktif;

    public Long getId() {
        return id;
    }

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }

    public String getSatuan() {
        return satuan;
    }

    public BigDecimal getHargaJual() {
        return hargaJual;
    }

    public boolean isAktif() {
        return aktif;
    }
}
