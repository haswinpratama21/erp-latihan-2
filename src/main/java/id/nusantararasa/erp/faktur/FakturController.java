package id.nusantararasa.erp.faktur;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/faktur")
public class FakturController {

    private final FakturService fakturService;

    public FakturController(FakturService fakturService) {
        this.fakturService = fakturService;
    }

    @PostMapping("/{soId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FakturDto terbitkan(@PathVariable Long soId) {
        return fakturService.terbitkan(soId);
    }

    @GetMapping
    public List<FakturDto> daftar() {
        return fakturService.daftar();
    }
}
