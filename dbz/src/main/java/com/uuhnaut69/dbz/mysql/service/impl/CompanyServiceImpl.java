package com.uuhnaut69.dbz.mysql.service.impl;

import com.github.javafaker.Faker;
import com.uuhnaut69.dbz.common.exception.NotFoundException;
import com.uuhnaut69.dbz.mysql.model.Company;
import com.uuhnaut69.dbz.mysql.model.Job;
import com.uuhnaut69.dbz.mysql.payload.request.CompanyRequest;
import com.uuhnaut69.dbz.mysql.repository.CompanyRepository;
import com.uuhnaut69.dbz.mysql.service.CompanyService;
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
            companies.add(Company.builder()
                    .name(faker.company().name())
                    .phone(faker.phoneNumber().phoneNumber())
                    .jobs(jobs)
                    .build());
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
