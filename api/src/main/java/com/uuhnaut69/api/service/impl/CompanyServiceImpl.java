package com.uuhnaut69.api.service.impl;

import com.github.javafaker.Faker;
import com.uuhnaut69.api.common.exception.NotFoundException;
import com.uuhnaut69.api.model.Company;
import com.uuhnaut69.api.model.Job;
import com.uuhnaut69.api.payload.request.CompanyRequest;
import com.uuhnaut69.api.repository.CompanyRepository;
import com.uuhnaut69.api.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public List<Company> dummyData() {
        Faker faker = new Faker();
        List<Company> companies = new ArrayList<>();
        IntStream.range(0, 5).forEach(i -> {
            Set<Job> jobs = new HashSet<>();
            jobs.add(Job.builder().title(faker.job().title()).build());
            Company company = new Company();
            company.setName(faker.company().name());
            company.setPhone(faker.phoneNumber().phoneNumber());
            company.setJobs(jobs);
            companies.add(company);
        });
        return companyRepository.saveAll(companies);
    }

    @Override
    public Company update(Long id, CompanyRequest companyRequest) {
        Company company = companyRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        company.setName(companyRequest.getName());
        company.setPhone(companyRequest.getPhone());
        return companyRepository.save(company);
    }

    @Override
    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }
}
