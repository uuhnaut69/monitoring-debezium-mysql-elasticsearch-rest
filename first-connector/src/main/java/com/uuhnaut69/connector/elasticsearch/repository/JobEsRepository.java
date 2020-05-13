package com.uuhnaut69.connector.elasticsearch.repository;

import com.uuhnaut69.connector.elasticsearch.document.JobEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Repository
public interface JobEsRepository extends ElasticsearchRepository<JobEs, String> {

    void deleteAllByCompanyEs_Id(String companyId);

}
