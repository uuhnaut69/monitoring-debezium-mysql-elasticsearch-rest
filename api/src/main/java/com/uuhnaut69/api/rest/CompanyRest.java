package com.uuhnaut69.api.rest;

import com.uuhnaut69.api.model.Company;
import com.uuhnaut69.api.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
