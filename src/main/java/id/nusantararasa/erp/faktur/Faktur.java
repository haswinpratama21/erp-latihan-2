package id.nusantararasa.erp.faktur;

import java.math.BigDecimal;
import java.time.LocalDate;

import id.nusantararasa.erp.customer.Customer;
import id.nusantararasa.erp.penjualan.SalesOrder;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "faktur")
public class Faktur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomor;
    private LocalDate tanggal;
    private LocalDate jatuhTempo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private StatusFaktur status;

    public Long getId() {
        return id;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalDate getJatuhTempo() {
        return jatuhTempo;
    }

    public void setJatuhTempo(LocalDate jatuhTempo) {
        this.jatuhTempo = jatuhTempo;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public StatusFaktur getStatus() {
        return status;
    }

    public void setStatus(StatusFaktur status) {
        this.status = status;
    }
}
