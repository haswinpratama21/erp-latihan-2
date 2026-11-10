package id.nusantararasa.erp.penjualan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

/**
 * Contoh:
 * {"kodeCustomer": "CUS-021", "diskonTambahan": 1.0, "item": [{"kodeProduk": "BSK-001", "qty": 100}]}
 */
public record BuatSalesOrderRequest(
        @NotBlank String kodeCustomer,
        LocalDate tanggal,
        BigDecimal diskonTambahan,
        @NotEmpty List<@Valid Item> item,
        String catatan) {

    public record Item(@NotBlank String kodeProduk, @Positive int qty) {
    }
}
