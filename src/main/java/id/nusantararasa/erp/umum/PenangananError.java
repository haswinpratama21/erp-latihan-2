package id.nusantararasa.erp.umum;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PenangananError {

    @ExceptionHandler(AturanBisnisException.class)
    public ResponseEntity<Map<String, String>> aturanBisnis(AturanBisnisException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("pesan", e.getMessage()));
    }

    @ExceptionHandler(DataTidakDitemukanException.class)
    public ResponseEntity<Map<String, String>> tidakDitemukan(DataTidakDitemukanException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("pesan", e.getMessage()));
    }
}
