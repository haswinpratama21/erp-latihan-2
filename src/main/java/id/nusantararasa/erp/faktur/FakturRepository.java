package id.nusantararasa.erp.faktur;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FakturRepository extends JpaRepository<Faktur, Long> {

    boolean existsBySalesOrderId(Long salesOrderId);

    List<Faktur> findAllByOrderByIdDesc();

    @Query("select coalesce(sum(f.total), 0) from Faktur f where f.customer.id = :customerId and f.status = :status")
    BigDecimal totalPiutang(@Param("customerId") Long customerId, @Param("status") StatusFaktur status);
}
