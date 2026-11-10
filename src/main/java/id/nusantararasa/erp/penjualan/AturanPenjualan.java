package id.nusantararasa.erp.penjualan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import id.nusantararasa.erp.customer.Segmen;
import id.nusantararasa.erp.umum.AturanBisnisException;
import id.nusantararasa.erp.umum.Rupiah;

/**
 * Aturan penjualan sesuai KBJ-PJL-002 (harga dan diskon) dan KBJ-KEU-004 (kredit).
 * Lihat docs/aturan-bisnis.md.
 */
public final class AturanPenjualan {

    public static final BigDecimal BATAS_DISKON_SUPERVISOR = new BigDecimal("1.5");
    public static final BigDecimal BATAS_DISKON_MAKS = new BigDecimal("3.0");

    private static final BigDecimal SERATUS = new BigDecimal("100");

    private static final Map<Segmen, BigDecimal> DISKON_SEGMEN = Map.of(
            Segmen.DISTRIBUTOR, new BigDecimal("12.00"),
            Segmen.RETAIL_MODERN, new BigDecimal("6.00"),
            Segmen.TOKO, new BigDecimal("2.00"));

    public record Keputusan(StatusSalesOrder status, String alasan) {
    }

    private AturanPenjualan() {
    }

    public static BigDecimal diskonStandar(Segmen segmen) {
        return DISKON_SEGMEN.get(segmen);
    }

    /** Harga x qty dikurangi diskon (persen), dibulatkan ke 2 desimal. */
    public static BigDecimal subtotal(int qty, BigDecimal harga, BigDecimal diskonPersen) {
        BigDecimal bruto = harga.multiply(BigDecimal.valueOf(qty));
        BigDecimal faktor = SERATUS.subtract(diskonPersen).divide(SERATUS);
        return bruto.multiply(faktor).setScale(2, RoundingMode.HALF_UP);
    }

    /** Diskon tambahan 0 sampai 3%. Di atas 3% wajib persetujuan tertulis Direktur di luar sistem. */
    public static void validasiDiskonTambahan(BigDecimal diskonTambahan) {
        if (diskonTambahan.signum() < 0) {
            throw new AturanBisnisException("Diskon tambahan tidak boleh negatif.");
        }
        if (diskonTambahan.compareTo(BATAS_DISKON_MAKS) > 0) {
            throw new AturanBisnisException("Diskon tambahan maksimal 3%. Di atas itu wajib persetujuan tertulis "
                    + "Direktur Operasional di luar sistem (KBJ-PJL-002).");
        }
    }

    /**
     * Status awal sales order. Limit kredit diperiksa lebih dulu karena keputusannya ada di Finance;
     * persetujuan diskon baru relevan kalau kreditnya aman.
     */
    public static Keputusan tentukanStatus(BigDecimal diskonTambahan, BigDecimal totalSo, BigDecimal piutang,
            BigDecimal limitKredit) {
        BigDecimal setelahSo = piutang.add(totalSo);
        if (setelahSo.compareTo(limitKredit) > 0) {
            return new Keputusan(StatusSalesOrder.DITAHAN,
                    "Melebihi limit kredit: piutang " + Rupiah.format(piutang) + " + SO ini " + Rupiah.format(totalSo)
                            + " = " + Rupiah.format(setelahSo) + ", limit " + Rupiah.format(limitKredit)
                            + ". Perlu persetujuan Divisi Finance.");
        }
        if (diskonTambahan.compareTo(BATAS_DISKON_SUPERVISOR) > 0) {
            return new Keputusan(StatusSalesOrder.MENUNGGU_PERSETUJUAN,
                    "Diskon tambahan di atas 1,5% sampai 3% wajib disetujui Manajer Penjualan (KBJ-PJL-002).");
        }
        return new Keputusan(StatusSalesOrder.SIAP_DIFAKTURKAN, null);
    }
}
