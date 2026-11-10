package id.nusantararasa.erp.produk;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdukRepository extends JpaRepository<Produk, Long> {

    Optional<Produk> findByKode(String kode);

    List<Produk> findByAktifTrueOrderByKodeAsc();
}
