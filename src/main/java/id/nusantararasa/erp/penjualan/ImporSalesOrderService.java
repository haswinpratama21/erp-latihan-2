package id.nusantararasa.erp.penjualan;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.nusantararasa.erp.customer.Customer;
import id.nusantararasa.erp.customer.CustomerRepository;
import id.nusantararasa.erp.produk.Produk;
import id.nusantararasa.erp.produk.ProdukRepository;
import id.nusantararasa.erp.umum.AturanBisnisException;

/**
 * Memindahkan sales order yang masih terbuka dari ekspor CSV sistem lama.
 *
 * Kolom: nomor_lama,tanggal,kode_customer,kode_produk,qty,harga (satu baris per produk).
 * Sistem lama tidak mengenal diskon tambahan dan persetujuannya sudah selesai di sana,
 * jadi SO hasil impor langsung berstatus SIAP_DIFAKTURKAN.
 */
@Service
public class ImporSalesOrderService {

    private static final CSVFormat FORMAT_CSV = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .build();

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProdukRepository produkRepository;

    public ImporSalesOrderService(SalesOrderRepository salesOrderRepository, CustomerRepository customerRepository,
            ProdukRepository produkRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.customerRepository = customerRepository;
        this.produkRepository = produkRepository;
    }

    @Transactional
    public List<SalesOrderDto> impor(String csv) {
        Map<String, SalesOrder> perNomor = new LinkedHashMap<>();
        try (CSVParser parser = FORMAT_CSV.parse(new StringReader(csv))) {
            for (CSVRecord r : parser) {
                SalesOrder so = perNomor.computeIfAbsent(r.get("nomor_lama"), nomor -> soBaru(nomor, r));
                Produk produk = produkRepository.findByKode(r.get("kode_produk"))
                        .orElseThrow(() -> new AturanBisnisException(
                                "Baris " + r.getRecordNumber() + ": produk " + r.get("kode_produk") + " tidak dikenal."));
                int qty = Integer.parseInt(r.get("qty"));
                BigDecimal harga = new BigDecimal(r.get("harga"));
                so.tambahItem(new SalesOrderItem(produk, qty, harga));
                so.setTotal(so.getTotal().add(AturanPenjualan.subtotal(qty, harga, so.getDiskonStandar())));
            }
        } catch (IOException | IllegalArgumentException | DateTimeParseException e) {
            throw new AturanBisnisException("File CSV tidak bisa dibaca: " + e.getMessage());
        }

        List<SalesOrderDto> hasil = new ArrayList<>();
        for (SalesOrder so : perNomor.values()) {
            hasil.add(SalesOrderDto.dari(salesOrderRepository.save(so)));
        }
        return hasil;
    }

    private SalesOrder soBaru(String nomorLama, CSVRecord r) {
        if (salesOrderRepository.existsByNomor(nomorLama)) {
            throw new AturanBisnisException("Baris " + r.getRecordNumber() + ": " + nomorLama + " sudah pernah diimpor.");
        }
        Customer customer = customerRepository.findByKode(r.get("kode_customer"))
                .orElseThrow(() -> new AturanBisnisException(
                        "Baris " + r.getRecordNumber() + ": customer " + r.get("kode_customer") + " tidak dikenal."));
        SalesOrder so = new SalesOrder();
        so.setNomor(nomorLama);
        so.setTanggal(LocalDate.parse(r.get("tanggal")));
        so.setCustomer(customer);
        so.setSumber(SumberSalesOrder.IMPOR);
        so.setDiskonStandar(AturanPenjualan.diskonStandar(customer.getSegmen()));
        so.setTotal(BigDecimal.ZERO);
        so.setStatus(StatusSalesOrder.SIAP_DIFAKTURKAN);
        so.setCatatan("Impor dari sistem lama");
        return so;
    }
}
