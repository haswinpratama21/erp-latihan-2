package id.nusantararasa.erp.umum;

/** Pelanggaran aturan bisnis. Pesannya ditampilkan ke pengguna (HTTP 422). */
public class AturanBisnisException extends RuntimeException {

    public AturanBisnisException(String pesan) {
        super(pesan);
    }
}
