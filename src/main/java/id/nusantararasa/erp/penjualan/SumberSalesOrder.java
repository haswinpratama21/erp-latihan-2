package id.nusantararasa.erp.penjualan;

public enum SumberSalesOrder {
    /** Dibuat lewat aplikasi ini (POST /api/sales-orders). */
    APLIKASI,
    /** Dipindahkan dari ekspor CSV sistem lama (POST /api/sales-orders/impor). */
    IMPOR
}
