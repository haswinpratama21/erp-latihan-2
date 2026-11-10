package id.nusantararasa.erp.penjualan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import id.nusantararasa.erp.customer.Customer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales_order")
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomor;
    private LocalDate tanggal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private SumberSalesOrder sumber;

    /** Persen, dari segmen customer saat SO dibuat. */
    private BigDecimal diskonStandar;

    /** Persen, diajukan sales di atas diskon standar. */
    private BigDecimal diskonTambahan;

    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private StatusSalesOrder status;

    private String alasanStatus;
    private String disetujuiOleh;
    private String catatan;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<SalesOrderItem> item = new ArrayList<>();

    public void tambahItem(SalesOrderItem baris) {
        baris.setSalesOrder(this);
        item.add(baris);
    }

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public SumberSalesOrder getSumber() {
        return sumber;
    }

    public void setSumber(SumberSalesOrder sumber) {
        this.sumber = sumber;
    }

    public BigDecimal getDiskonStandar() {
        return diskonStandar;
    }

    public void setDiskonStandar(BigDecimal diskonStandar) {
        this.diskonStandar = diskonStandar;
    }

    public BigDecimal getDiskonTambahan() {
        return diskonTambahan;
    }

    public void setDiskonTambahan(BigDecimal diskonTambahan) {
        this.diskonTambahan = diskonTambahan;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public StatusSalesOrder getStatus() {
        return status;
    }

    public void setStatus(StatusSalesOrder status) {
        this.status = status;
    }

    public String getAlasanStatus() {
        return alasanStatus;
    }

    public void setAlasanStatus(String alasanStatus) {
        this.alasanStatus = alasanStatus;
    }

    public String getDisetujuiOleh() {
        return disetujuiOleh;
    }

    public void setDisetujuiOleh(String disetujuiOleh) {
        this.disetujuiOleh = disetujuiOleh;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public List<SalesOrderItem> getItem() {
        return item;
    }
}
