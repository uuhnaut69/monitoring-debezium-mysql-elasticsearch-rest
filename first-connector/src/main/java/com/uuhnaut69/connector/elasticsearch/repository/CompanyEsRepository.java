package com.uuhnaut69.connector.elasticsearch.repository;

import com.uuhnaut69.connector.elasticsearch.document.CompanyEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Repository
public interface CompanyEsRepository extends ElasticsearchRepository<CompanyEs, String> {
}
