package id.nusantararasa.erp.umum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public final class Rupiah {

    private Rupiah() {
    }

    /** Contoh: 12345000.50 menjadi "Rp 12.345.001". */
    public static String format(BigDecimal nilai) {
        NumberFormat f = NumberFormat.getIntegerInstance(Locale.forLanguageTag("id-ID"));
        return "Rp " + f.format(nilai.setScale(0, RoundingMode.HALF_UP));
    }
}
