package id.nusantararasa.erp.penjualan;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    boolean existsByNomor(String nomor);

    List<SalesOrder> findAllByOrderByIdDesc();

    List<SalesOrder> findByStatusOrderByIdDesc(StatusSalesOrder status);
}
