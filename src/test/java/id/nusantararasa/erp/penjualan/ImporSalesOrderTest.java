package id.nusantararasa.erp.penjualan;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ImporSalesOrderTest {

    private static final String CSV = """
            nomor_lama,tanggal,kode_customer,kode_produk,qty,harga
            ERP3-SO-2026-0470,2026-10-01,CUS-030,BSK-001,100,9000
            ERP3-SO-2026-0470,2026-10-01,CUS-030,MNM-001,200,4800
            ERP3-SO-2026-0471,2026-10-01,CUS-002,ROT-002,1000,6200
            """;

    @Autowired
    MockMvc mvc;

    @Test
    void imporMembuatSatuSoPerNomorLama() throws Exception {
        mvc.perform(post("/api/sales-orders/impor").contentType("text/csv").content(CSV))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nomor").value("ERP3-SO-2026-0470"))
                .andExpect(jsonPath("$[0].sumber").value("IMPOR"))
                .andExpect(jsonPath("$[0].status").value("SIAP_DIFAKTURKAN"))
                .andExpect(jsonPath("$[0].item.length()").value(2))
                .andExpect(jsonPath("$[0].total").value(1822800.0))
                .andExpect(jsonPath("$[1].total").value(5456000.0));
    }

    @Test
    void nomorYangSudahAdaTidakDiimporLagi() throws Exception {
        mvc.perform(post("/api/sales-orders/impor").contentType("text/csv").content("""
                nomor_lama,tanggal,kode_customer,kode_produk,qty,harga
                ERP3-SO-2026-0451,2026-09-29,CUS-004,BSK-001,10,9500
                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void customerTidakDikenalDitolak() throws Exception {
        mvc.perform(post("/api/sales-orders/impor").contentType("text/csv").content("""
                nomor_lama,tanggal,kode_customer,kode_produk,qty,harga
                ERP3-SO-2026-0480,2026-10-01,CUS-999,BSK-001,10,9500
                """))
                .andExpect(status().isUnprocessableEntity());
    }
}
