package id.nusantararasa.erp.faktur;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.nusantararasa.erp.penjualan.AturanPenjualan;
import id.nusantararasa.erp.penjualan.SalesOrder;
import id.nusantararasa.erp.penjualan.SalesOrderItem;
import id.nusantararasa.erp.penjualan.SalesOrderRepository;
import id.nusantararasa.erp.penjualan.StatusSalesOrder;
import id.nusantararasa.erp.umum.AturanBisnisException;
import id.nusantararasa.erp.umum.DataTidakDitemukanException;
import id.nusantararasa.erp.umum.Penomoran;

@Service
public class FakturService {

    private static final ZoneId WIB = ZoneId.of("Asia/Jakarta");

    private final FakturRepository fakturRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final Penomoran penomoran;

    public FakturService(FakturRepository fakturRepository, SalesOrderRepository salesOrderRepository,
            Penomoran penomoran) {
        this.fakturRepository = fakturRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.penomoran = penomoran;
    }

    /** Terbitkan faktur untuk SO yang sudah siap. Total dihitung ulang dari baris SO. */
    @Transactional
    public FakturDto terbitkan(Long soId) {
        SalesOrder so = salesOrderRepository.findById(soId)
                .orElseThrow(() -> new DataTidakDitemukanException("Sales order " + soId + " tidak ditemukan."));
        if (fakturRepository.existsBySalesOrderId(soId)) {
            throw new AturanBisnisException("Sales order " + so.getNomor() + " sudah difakturkan.");
        }
        if (so.getStatus() != StatusSalesOrder.SIAP_DIFAKTURKAN) {
            throw new AturanBisnisException("Sales order " + so.getNomor() + " berstatus " + so.getStatus()
                    + ", belum bisa difakturkan.");
        }

        BigDecimal diskon = so.getDiskonStandar().add(so.getDiskonTambahan());
        List<FakturDto.Baris> rincian = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (SalesOrderItem i : so.getItem()) {
            BigDecimal subtotal = AturanPenjualan.subtotal(i.getQty(), i.getHarga(), diskon);
            rincian.add(new FakturDto.Baris(i.getProduk().getKode(), i.getProduk().getNama(), i.getQty(), i.getHarga(),
                    diskon, subtotal));
            total = total.add(subtotal);
        }

        LocalDate hariIni = LocalDate.now(WIB);
        Faktur faktur = new Faktur();
        faktur.setNomor(penomoran.nomorFaktur(hariIni));
        faktur.setTanggal(hariIni);
        faktur.setJatuhTempo(hariIni.plusDays(so.getCustomer().getTerminHari()));
        faktur.setCustomer(so.getCustomer());
        faktur.setSalesOrder(so);
        faktur.setTotal(total);
        faktur.setStatus(StatusFaktur.BELUM_LUNAS);
        fakturRepository.save(faktur);

        so.setStatus(StatusSalesOrder.DIFAKTURKAN);
        return FakturDto.dari(faktur, rincian);
    }

    @Transactional(readOnly = true)
    public List<FakturDto> daftar() {
        return fakturRepository.findAllByOrderByIdDesc().stream()
                .map(f -> FakturDto.dari(f, List.of()))
                .toList();
    }
}
