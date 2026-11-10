package id.nusantararasa.erp.penjualan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SalesOrderDto(
        Long id,
        String nomor,
        LocalDate tanggal,
        String kodeCustomer,
        String namaCustomer,
        SumberSalesOrder sumber,
        BigDecimal diskonStandar,
        BigDecimal diskonTambahan,
        BigDecimal total,
        StatusSalesOrder status,
        String alasanStatus,
        String disetujuiOleh,
        List<Item> item) {

    public record Item(String kodeProduk, String namaProduk, int qty, BigDecimal harga) {
    }

    public static SalesOrderDto dari(SalesOrder so) {
        List<Item> item = so.getItem().stream()
                .map(i -> new Item(i.getProduk().getKode(), i.getProduk().getNama(), i.getQty(), i.getHarga()))
                .toList();
        return new SalesOrderDto(so.getId(), so.getNomor(), so.getTanggal(), so.getCustomer().getKode(),
                so.getCustomer().getNama(), so.getSumber(), so.getDiskonStandar(), so.getDiskonTambahan(),
                so.getTotal(), so.getStatus(), so.getAlasanStatus(), so.getDisetujuiOleh(), item);
    }
}
