package com.uuhnaut69.dbz.mysql.service;

import com.uuhnaut69.dbz.mysql.model.Company;
import com.uuhnaut69.dbz.mysql.payload.request.CompanyRequest;

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
