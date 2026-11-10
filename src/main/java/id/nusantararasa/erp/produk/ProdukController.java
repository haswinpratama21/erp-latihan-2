package id.nusantararasa.erp.produk;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produk")
public class ProdukController {

    public record ProdukDto(String kode, String nama, String satuan, BigDecimal hargaJual) {
    }

    private final ProdukRepository produkRepository;

    public ProdukController(ProdukRepository produkRepository) {
        this.produkRepository = produkRepository;
    }

    @GetMapping
    public List<ProdukDto> daftar() {
        return produkRepository.findByAktifTrueOrderByKodeAsc().stream()
                .map(p -> new ProdukDto(p.getKode(), p.getNama(), p.getSatuan(), p.getHargaJual()))
                .toList();
    }
}
