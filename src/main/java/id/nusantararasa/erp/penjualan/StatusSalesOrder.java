package id.nusantararasa.erp.penjualan;

public enum StatusSalesOrder {
    /** Lolos semua aturan, atau sudah disetujui. Boleh dibuatkan faktur. */
    SIAP_DIFAKTURKAN,
    /** Diskon tambahan di atas 1,5% menunggu persetujuan Manajer Penjualan. */
    MENUNGGU_PERSETUJUAN,
    /** Melebihi limit kredit, menunggu keputusan Finance. */
    DITAHAN,
    /** Faktur sudah terbit. */
    DIFAKTURKAN
}
