package id.nusantararasa.erp.penjualan;

import java.math.BigDecimal;

import id.nusantararasa.erp.produk.Produk;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales_order_item")
public class SalesOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produk_id")
    private Produk produk;

    private int qty;

    /** Harga satuan sebelum diskon, dicatat saat SO dibuat. */
    private BigDecimal harga;

    protected SalesOrderItem() {
    }

    public SalesOrderItem(Produk produk, int qty, BigDecimal harga) {
        this.produk = produk;
        this.qty = qty;
        this.harga = harga;
    }

    public Long getId() {
        return id;
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }

    void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }

    public Produk getProduk() {
        return produk;
    }

    public int getQty() {
        return qty;
    }

    public BigDecimal getHarga() {
        return harga;
    }
}
