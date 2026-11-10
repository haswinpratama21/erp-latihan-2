package id.nusantararasa.erp.penjualan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import id.nusantararasa.erp.customer.Segmen;
import id.nusantararasa.erp.umum.AturanBisnisException;

class AturanPenjualanTest {

    private static final BigDecimal LIMIT = new BigDecimal("40000000");

    @Test
    void diskonStandarMengikutiSegmen() {
        assertThat(AturanPenjualan.diskonStandar(Segmen.DISTRIBUTOR)).isEqualByComparingTo("12");
        assertThat(AturanPenjualan.diskonStandar(Segmen.RETAIL_MODERN)).isEqualByComparingTo("6");
        assertThat(AturanPenjualan.diskonStandar(Segmen.TOKO)).isEqualByComparingTo("2");
    }

    @Test
    void subtotalDikurangiDiskonDanDibulatkan() {
        assertThat(AturanPenjualan.subtotal(100, new BigDecimal("9500"), new BigDecimal("2")))
                .isEqualByComparingTo("931000.00");
        assertThat(AturanPenjualan.subtotal(3, new BigDecimal("6500"), new BigDecimal("8.5")))
                .isEqualByComparingTo("17842.50");
    }

    @Test
    void diskonTambahanSampai3PersenDiterima() {
        AturanPenjualan.validasiDiskonTambahan(BigDecimal.ZERO);
        AturanPenjualan.validasiDiskonTambahan(new BigDecimal("3.0"));
    }

    @Test
    void diskonTambahanDiAtas3PersenAtauNegatifDitolak() {
        assertThatThrownBy(() -> AturanPenjualan.validasiDiskonTambahan(new BigDecimal("3.01")))
                .isInstanceOf(AturanBisnisException.class)
                .hasMessageContaining("maksimal 3%");
        assertThatThrownBy(() -> AturanPenjualan.validasiDiskonTambahan(new BigDecimal("-1")))
                .isInstanceOf(AturanBisnisException.class);
    }

    @Test
    void diskonSampai1Koma5PersenTidakPerluPersetujuan() {
        var hasil = AturanPenjualan.tentukanStatus(new BigDecimal("1.5"), new BigDecimal("1000000"),
                BigDecimal.ZERO, LIMIT);
        assertThat(hasil.status()).isEqualTo(StatusSalesOrder.SIAP_DIFAKTURKAN);
        assertThat(hasil.alasan()).isNull();
    }

    @Test
    void diskonDiAtas1Koma5PersenMenungguManajerPenjualan() {
        var hasil = AturanPenjualan.tentukanStatus(new BigDecimal("1.6"), new BigDecimal("1000000"),
                BigDecimal.ZERO, LIMIT);
        assertThat(hasil.status()).isEqualTo(StatusSalesOrder.MENUNGGU_PERSETUJUAN);
        assertThat(hasil.alasan()).contains("Manajer Penjualan");
    }

    @Test
    void melebihiLimitKreditDitahanWalaupunAdaDiskon() {
        var hasil = AturanPenjualan.tentukanStatus(new BigDecimal("2"), new BigDecimal("5000000"),
                new BigDecimal("36000000"), LIMIT);
        assertThat(hasil.status()).isEqualTo(StatusSalesOrder.DITAHAN);
        assertThat(hasil.alasan()).contains("Rp 41.000.000").contains("Finance");
    }

    @Test
    void tepatDiLimitKreditMasihBoleh() {
        var hasil = AturanPenjualan.tentukanStatus(BigDecimal.ZERO, new BigDecimal("4000000"),
                new BigDecimal("36000000"), LIMIT);
        assertThat(hasil.status()).isEqualTo(StatusSalesOrder.SIAP_DIFAKTURKAN);
    }
}
