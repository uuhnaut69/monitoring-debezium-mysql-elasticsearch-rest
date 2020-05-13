package com.uuhnaut69.connector.elasticsearch.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uuhnaut69.connector.elasticsearch.document.CompanyEs;
import com.uuhnaut69.connector.elasticsearch.document.JobEs;
import com.uuhnaut69.connector.elasticsearch.repository.JobEsRepository;
import com.uuhnaut69.connector.elasticsearch.service.CompanyEsService;
import com.uuhnaut69.connector.elasticsearch.service.JobEsService;
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
public class JobEsServiceImpl implements JobEsService {

    private final JobEsRepository jobEsRepository;

    private final CompanyEsService companyEsService;

    @Override
    public void handleEvent(Map<String, Object> jobData, Envelope.Operation operation) {
        final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        final JobEs jobEs = mapper.convertValue(jobData, JobEs.class);

        if (Envelope.Operation.DELETE.name().equals(operation.name())) {
            jobEsRepository.deleteById(jobEs.getId());
        } else {
            String companyId = jobData.get("company_id").toString();
            CompanyEs companyEs = companyEsService.findById(companyId);
            jobEs.setCompanyEs(companyEs);
            jobEsRepository.save(jobEs);
        }
    }
}
