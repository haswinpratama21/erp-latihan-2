package id.nusantararasa.erp.faktur;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FakturApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void terbitkanFakturUntukSoYangSiap() throws Exception {
        // SO 11: CUS-003 (Distributor, termin 45 hari), diskon standar 12%
        String jatuhTempo = LocalDate.now(ZoneId.of("Asia/Jakarta")).plusDays(45).toString();
        mvc.perform(post("/api/faktur/11"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomor").value(startsWith("INV/")))
                .andExpect(jsonPath("$.nomorSalesOrder").value("SO/2026/10/00011"))
                .andExpect(jsonPath("$.jatuhTempo").value(jatuhTempo))
                .andExpect(jsonPath("$.rincian.length()").value(3))
                .andExpect(jsonPath("$.total").value(49456000.0))
                .andExpect(jsonPath("$.status").value("BELUM_LUNAS"));

        mvc.perform(get("/api/sales-orders/11"))
                .andExpect(jsonPath("$.status").value("DIFAKTURKAN"));
    }

    @Test
    void totalFakturSamaDenganTotalSo() throws Exception {
        String so = mvc.perform(post("/api/sales-orders").contentType(MediaType.APPLICATION_JSON).content("""
                {"kodeCustomer": "CUS-031", "diskonTambahan": 1.5,
                 "item": [{"kodeProduk": "ROT-002", "qty": 120}, {"kodeProduk": "MNM-001", "qty": 48}]}
                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(so, "$.id");
        Number totalSo = JsonPath.read(so, "$.total");

        mvc.perform(post("/api/faktur/" + id))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(totalSo.doubleValue()))
                .andExpect(jsonPath("$.rincian[0].diskonPersen").value(3.5));
    }

    @Test
    void soYangSudahDifakturkanTidakBisaDifakturkanLagi() throws Exception {
        mvc.perform(post("/api/faktur/12")).andExpect(status().isCreated());
        mvc.perform(post("/api/faktur/12")).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void soYangMenungguPersetujuanBelumBisaDifakturkan() throws Exception {
        mvc.perform(post("/api/faktur/9"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.pesan").value(org.hamcrest.Matchers.containsString("MENUNGGU_PERSETUJUAN")));
    }

    @Test
    void soTidakAda() throws Exception {
        mvc.perform(post("/api/faktur/9999")).andExpect(status().isNotFound());
    }
}
