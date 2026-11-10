package id.nusantararasa.erp;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ErpLatihanApplicationTests {

    @Autowired
    MockMvc mvc;

    @Test
    void dokumentasiApiTersedia() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/faktur/{soId}")));
    }

    @Test
    void versiDokumenOpenApi() throws Exception {
        // Kontrak API: versi dokumen OpenAPI yang dipublikasikan dikunci di sini.
        // Konsumen dokumen (generator client, Postman, dll.) bergantung pada versi ini,
        // jadi perubahannya harus disengaja dan diberitahukan.
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").value("3.1.0"));
    }
}
