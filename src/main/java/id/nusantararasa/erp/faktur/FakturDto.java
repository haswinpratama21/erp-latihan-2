package id.nusantararasa.erp.faktur;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FakturDto(
        Long id,
        String nomor,
        LocalDate tanggal,
        LocalDate jatuhTempo,
        String kodeCustomer,
        String namaCustomer,
        String nomorSalesOrder,
        BigDecimal total,
        StatusFaktur status,
        List<Baris> rincian) {

    public record Baris(String kodeProduk, String namaProduk, int qty, BigDecimal harga, BigDecimal diskonPersen,
            BigDecimal subtotal) {
    }

    public static FakturDto dari(Faktur f, List<Baris> rincian) {
        return new FakturDto(f.getId(), f.getNomor(), f.getTanggal(), f.getJatuhTempo(), f.getCustomer().getKode(),
                f.getCustomer().getNama(), f.getSalesOrder().getNomor(), f.getTotal(), f.getStatus(), rincian);
    }
}
