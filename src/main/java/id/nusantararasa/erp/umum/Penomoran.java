package id.nusantararasa.erp.umum;

import java.time.LocalDate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Nomor dokumen berurutan, mis. SO/2026/11/00101 dan INV/2026/11/00101. */
@Component
public class Penomoran {

    private final JdbcTemplate jdbc;

    public Penomoran(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String nomorSalesOrder(LocalDate tanggal) {
        return nomor("SO", "nomor_so_seq", tanggal);
    }

    public String nomorFaktur(LocalDate tanggal) {
        return nomor("INV", "nomor_faktur_seq", tanggal);
    }

    private String nomor(String prefix, String sequence, LocalDate tanggal) {
        Long urut = jdbc.queryForObject("SELECT NEXT VALUE FOR " + sequence, Long.class);
        return String.format("%s/%d/%02d/%05d", prefix, tanggal.getYear(), tanggal.getMonthValue(), urut);
    }
}
