package id.nusantararasa.erp.penjualan;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderController {

    public record PersetujuanRequest(@NotBlank String disetujuiOleh) {
    }

    private final SalesOrderService salesOrderService;
    private final ImporSalesOrderService imporSalesOrderService;

    public SalesOrderController(SalesOrderService salesOrderService, ImporSalesOrderService imporSalesOrderService) {
        this.salesOrderService = salesOrderService;
        this.imporSalesOrderService = imporSalesOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalesOrderDto buat(@Valid @RequestBody BuatSalesOrderRequest req) {
        return salesOrderService.buat(req);
    }

    @GetMapping
    public List<SalesOrderDto> daftar(@RequestParam(required = false) StatusSalesOrder status) {
        return salesOrderService.daftar(status);
    }

    @GetMapping("/{id}")
    public SalesOrderDto detail(@PathVariable Long id) {
        return salesOrderService.detail(id);
    }

    @PostMapping("/{id}/setujui")
    public SalesOrderDto setujui(@PathVariable Long id, @Valid @RequestBody PersetujuanRequest req) {
        return salesOrderService.setujui(id, req.disetujuiOleh());
    }

    @PostMapping(path = "/impor", consumes = "text/csv")
    @ResponseStatus(HttpStatus.CREATED)
    public List<SalesOrderDto> impor(@RequestBody String csv) {
        return imporSalesOrderService.impor(csv);
    }
}
