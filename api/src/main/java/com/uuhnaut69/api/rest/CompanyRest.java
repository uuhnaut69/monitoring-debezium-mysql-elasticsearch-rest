package com.uuhnaut69.api.rest;

import com.uuhnaut69.api.model.Company;
import com.uuhnaut69.api.payload.request.CompanyRequest;
import com.uuhnaut69.api.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyRest {

    private final CompanyService companyService;

    @PostMapping
    public List<Company> dummyData() {
        return companyService.dummyData();
    }

    @PutMapping("/{companyId}")
    public Company update(@PathVariable Long companyId, @RequestBody CompanyRequest companyRequest) {
        return companyService.update(companyId, companyRequest);
    }

    @DeleteMapping("/{companyId}")
    public void delete(@PathVariable Long companyId) {
        companyService.deleteById(companyId);
    }

}
