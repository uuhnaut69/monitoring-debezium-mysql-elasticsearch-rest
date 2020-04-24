package com.uuhnaut69.dbz.elasticsearch.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uuhnaut69.dbz.common.exception.NotFoundException;
import com.uuhnaut69.dbz.elasticsearch.document.CompanyEs;
import com.uuhnaut69.dbz.elasticsearch.repository.CompanyEsRepository;
import com.uuhnaut69.dbz.elasticsearch.repository.JobEsRepository;
import com.uuhnaut69.dbz.elasticsearch.service.CompanyEsService;
import io.debezium.data.Envelope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Service
@RequiredArgsConstructor
public class CompanyEsServiceImpl implements CompanyEsService {

    private final CompanyEsRepository companyEsRepository;

    private final JobEsRepository jobEsRepository;

    @Override
    public CompanyEs findById(String companyId) {
        return companyEsRepository.findById(companyId).orElseThrow(() -> new NotFoundException("Not found !!!"));
    }

    @Override
    public void handleEvent(Map<String, Object> companyData, Envelope.Operation operation) {
        final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        final CompanyEs companyEs = mapper.convertValue(companyData, CompanyEs.class);

        if (Envelope.Operation.DELETE.name().equals(operation.name())) {
            companyEsRepository.deleteById(companyEs.getId());
            jobEsRepository.deleteAllByCompanyEs_Id(companyEs.getId());
        } else {
            companyEsRepository.save(companyEs);
        }

    }
}
