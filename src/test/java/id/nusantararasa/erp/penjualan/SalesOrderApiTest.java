package id.nusantararasa.erp.penjualan;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SalesOrderApiTest {

    @Autowired
    MockMvc mvc;

    private ResultActions buatSo(String json) throws Exception {
        return mvc.perform(post("/api/sales-orders").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void tanpaDiskonTambahanLangsungSiapDifakturkan() throws Exception {
        buatSo("""
                {"kodeCustomer": "CUS-022", "item": [{"kodeProduk": "BSK-001", "qty": 100}]}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomor").value(startsWith("SO/")))
                .andExpect(jsonPath("$.sumber").value("APLIKASI"))
                .andExpect(jsonPath("$.diskonStandar").value(2.0))
                .andExpect(jsonPath("$.diskonTambahan").value(0))
                .andExpect(jsonPath("$.total").value(931000.0))
                .andExpect(jsonPath("$.status").value("SIAP_DIFAKTURKAN"));
    }

    @Test
    void diskonTambahan1PersenMasihWewenangSupervisor() throws Exception {
        buatSo("""
                {"kodeCustomer": "CUS-022", "diskonTambahan": 1.0, "item": [{"kodeProduk": "BSK-001", "qty": 100}]}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(921500.0))
                .andExpect(jsonPath("$.status").value("SIAP_DIFAKTURKAN"));
    }

    @Test
    void diskonTambahan2PersenMenungguPersetujuan() throws Exception {
        buatSo("""
                {"kodeCustomer": "CUS-022", "diskonTambahan": 2.0, "item": [{"kodeProduk": "BSK-001", "qty": 100}]}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("MENUNGGU_PERSETUJUAN"))
                .andExpect(jsonPath("$.alasanStatus").value(containsString("Manajer Penjualan")));
    }

    @Test
    void diskonTambahanDiAtas3PersenDitolak() throws Exception {
        buatSo("""
                {"kodeCustomer": "CUS-022", "diskonTambahan": 3.5, "item": [{"kodeProduk": "BSK-001", "qty": 100}]}
                """)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.pesan").value(containsString("maksimal 3%")));
    }

    @Test
    void melebihiLimitKreditDitahan() throws Exception {
        // CUS-021 punya piutang Rp 36.750.000 dari limit Rp 40.000.000
        buatSo("""
                {"kodeCustomer": "CUS-021", "item": [{"kodeProduk": "BSK-004", "qty": 500}]}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(7105000.0))
                .andExpect(jsonPath("$.status").value("DITAHAN"));
    }

    @Test
    void produkYangSamaDuaKaliDitolak() throws Exception {
        buatSo("""
                {"kodeCustomer": "CUS-022", "item": [{"kodeProduk": "BSK-001", "qty": 1}, {"kodeProduk": "BSK-001", "qty": 2}]}
                """)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void requestTanpaItemDitolak() throws Exception {
        buatSo("""
                {"kodeCustomer": "CUS-022", "item": []}
                """)
                .andExpect(status().isBadRequest());
    }

    @Test
    void soYangMenungguBisaDisetujui() throws Exception {
        String body = buatSo("""
                {"kodeCustomer": "CUS-022", "diskonTambahan": 2.5, "item": [{"kodeProduk": "ROT-002", "qty": 50}]}
                """)
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(body, "$.id");

        mvc.perform(post("/api/sales-orders/" + id + "/setujui").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"disetujuiOleh": "Dewi Lestari"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SIAP_DIFAKTURKAN"))
                .andExpect(jsonPath("$.disetujuiOleh").value("Dewi Lestari"));
    }

    @Test
    void detailSoTidakAda() throws Exception {
        mvc.perform(get("/api/sales-orders/9999")).andExpect(status().isNotFound());
    }
}
