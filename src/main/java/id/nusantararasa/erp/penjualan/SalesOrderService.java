package id.nusantararasa.erp.penjualan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.nusantararasa.erp.customer.Customer;
import id.nusantararasa.erp.customer.CustomerRepository;
import id.nusantararasa.erp.faktur.FakturRepository;
import id.nusantararasa.erp.faktur.StatusFaktur;
import id.nusantararasa.erp.produk.Produk;
import id.nusantararasa.erp.produk.ProdukRepository;
import id.nusantararasa.erp.umum.AturanBisnisException;
import id.nusantararasa.erp.umum.DataTidakDitemukanException;
import id.nusantararasa.erp.umum.Penomoran;

@Service
public class SalesOrderService {

    private static final ZoneId WIB = ZoneId.of("Asia/Jakarta");

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProdukRepository produkRepository;
    private final FakturRepository fakturRepository;
    private final Penomoran penomoran;

    public SalesOrderService(SalesOrderRepository salesOrderRepository, CustomerRepository customerRepository,
            ProdukRepository produkRepository, FakturRepository fakturRepository, Penomoran penomoran) {
        this.salesOrderRepository = salesOrderRepository;
        this.customerRepository = customerRepository;
        this.produkRepository = produkRepository;
        this.fakturRepository = fakturRepository;
        this.penomoran = penomoran;
    }

    @Transactional
    public SalesOrderDto buat(BuatSalesOrderRequest req) {
        Customer customer = customerRepository.findByKode(req.kodeCustomer())
                .orElseThrow(() -> new DataTidakDitemukanException("Customer " + req.kodeCustomer() + " tidak ditemukan."));
        BigDecimal diskonTambahan = Objects.requireNonNullElse(req.diskonTambahan(), BigDecimal.ZERO);
        AturanPenjualan.validasiDiskonTambahan(diskonTambahan);

        LocalDate tanggal = req.tanggal() != null ? req.tanggal() : LocalDate.now(WIB);
        SalesOrder so = new SalesOrder();
        so.setNomor(penomoran.nomorSalesOrder(tanggal));
        so.setTanggal(tanggal);
        so.setCustomer(customer);
        so.setSumber(SumberSalesOrder.APLIKASI);
        so.setDiskonStandar(AturanPenjualan.diskonStandar(customer.getSegmen()));
        so.setDiskonTambahan(diskonTambahan);
        so.setCatatan(req.catatan());

        BigDecimal diskon = so.getDiskonStandar().add(diskonTambahan);
        BigDecimal total = BigDecimal.ZERO;
        Set<String> kodeDipakai = new HashSet<>();
        for (BuatSalesOrderRequest.Item baris : req.item()) {
            if (!kodeDipakai.add(baris.kodeProduk())) {
                throw new AturanBisnisException("Produk " + baris.kodeProduk() + " muncul dua kali. Gabungkan qty-nya.");
            }
            Produk produk = produkRepository.findByKode(baris.kodeProduk())
                    .filter(Produk::isAktif)
                    .orElseThrow(() -> new AturanBisnisException(
                            "Produk " + baris.kodeProduk() + " tidak ditemukan atau tidak aktif."));
            so.tambahItem(new SalesOrderItem(produk, baris.qty(), produk.getHargaJual()));
            total = total.add(AturanPenjualan.subtotal(baris.qty(), produk.getHargaJual(), diskon));
        }
        so.setTotal(total);

        BigDecimal piutang = fakturRepository.totalPiutang(customer.getId(), StatusFaktur.BELUM_LUNAS);
        AturanPenjualan.Keputusan keputusan = AturanPenjualan.tentukanStatus(diskonTambahan, total, piutang,
                customer.getLimitKredit());
        so.setStatus(keputusan.status());
        so.setAlasanStatus(keputusan.alasan());

        return SalesOrderDto.dari(salesOrderRepository.save(so));
    }

    @Transactional
    public SalesOrderDto setujui(Long id, String disetujuiOleh) {
        SalesOrder so = cari(id);
        if (so.getStatus() != StatusSalesOrder.MENUNGGU_PERSETUJUAN && so.getStatus() != StatusSalesOrder.DITAHAN) {
            throw new AturanBisnisException("Sales order " + so.getNomor() + " tidak sedang menunggu persetujuan.");
        }
        so.setStatus(StatusSalesOrder.SIAP_DIFAKTURKAN);
        so.setDisetujuiOleh(disetujuiOleh);
        return SalesOrderDto.dari(so);
    }

    @Transactional(readOnly = true)
    public SalesOrderDto detail(Long id) {
        return SalesOrderDto.dari(cari(id));
    }

    @Transactional(readOnly = true)
    public List<SalesOrderDto> daftar(StatusSalesOrder status) {
        List<SalesOrder> hasil = status == null
                ? salesOrderRepository.findAllByOrderByIdDesc()
                : salesOrderRepository.findByStatusOrderByIdDesc(status);
        return hasil.stream().map(SalesOrderDto::dari).toList();
    }

    private SalesOrder cari(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new DataTidakDitemukanException("Sales order " + id + " tidak ditemukan."));
    }
}
