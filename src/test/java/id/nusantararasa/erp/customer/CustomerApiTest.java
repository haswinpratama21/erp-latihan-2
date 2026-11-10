package id.nusantararasa.erp.customer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void daftarSemuaCustomer() throws Exception {
        mvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(50))
                .andExpect(jsonPath("$[0].kode").value("CUS-001"))
                .andExpect(jsonPath("$[0].segmen").value("DISTRIBUTOR"));
    }

    @Test
    void filterPerSegmen() throws Exception {
        mvc.perform(get("/api/customers").param("segmen", "TOKO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(25));
    }

    @Test
    void detailCustomer() throws Exception {
        mvc.perform(get("/api/customers/CUS-021"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nama").value("Toko Berkah Abadi"))
                .andExpect(jsonPath("$.terminHari").value(14))
                .andExpect(jsonPath("$.limitKredit").value(40000000.0));
    }

    @Test
    void customerTidakAda() throws Exception {
        mvc.perform(get("/api/customers/CUS-999")).andExpect(status().isNotFound());
    }
}
