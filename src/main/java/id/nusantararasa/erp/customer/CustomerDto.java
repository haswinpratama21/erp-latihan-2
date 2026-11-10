package id.nusantararasa.erp.customer;

import java.math.BigDecimal;

public record CustomerDto(
        String kode,
        String nama,
        String kota,
        Segmen segmen,
        int terminHari,
        BigDecimal limitKredit) {

    public static CustomerDto dari(Customer c) {
        return new CustomerDto(c.getKode(), c.getNama(), c.getKota(), c.getSegmen(), c.getTerminHari(),
                c.getLimitKredit());
    }
}
