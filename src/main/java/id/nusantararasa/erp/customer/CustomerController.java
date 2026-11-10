package id.nusantararasa.erp.customer;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.nusantararasa.erp.umum.DataTidakDitemukanException;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<CustomerDto> daftar(@RequestParam(required = false) Segmen segmen) {
        List<Customer> hasil = segmen == null
                ? customerRepository.findAllByOrderByKodeAsc()
                : customerRepository.findBySegmenOrderByKodeAsc(segmen);
        return hasil.stream().map(CustomerDto::dari).toList();
    }

    @GetMapping("/{kode}")
    public CustomerDto detail(@PathVariable String kode) {
        return customerRepository.findByKode(kode)
                .map(CustomerDto::dari)
                .orElseThrow(() -> new DataTidakDitemukanException("Customer " + kode + " tidak ditemukan."));
    }
}
