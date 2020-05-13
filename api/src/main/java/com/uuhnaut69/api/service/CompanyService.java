package com.uuhnaut69.api.service;

import com.uuhnaut69.api.model.Company;
import com.uuhnaut69.api.payload.request.CompanyRequest;

import java.util.List;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
public interface CompanyService {

    List<Company> dummyData();

    Company update(Long id, CompanyRequest companyRequest);

    void deleteById(Long id);
}
