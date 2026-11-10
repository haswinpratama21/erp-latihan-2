package id.nusantararasa.erp.umum;

/** Data yang diminta tidak ada (HTTP 404). */
public class DataTidakDitemukanException extends RuntimeException {

    public DataTidakDitemukanException(String pesan) {
        super(pesan);
    }
}
