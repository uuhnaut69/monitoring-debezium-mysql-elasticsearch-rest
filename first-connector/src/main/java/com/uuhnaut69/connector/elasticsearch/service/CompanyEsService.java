package com.uuhnaut69.connector.elasticsearch.service;

import com.uuhnaut69.connector.elasticsearch.document.CompanyEs;
import io.debezium.data.Envelope;

import java.util.Map;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
public interface CompanyEsService {

    CompanyEs findById(String companyId);

    void handleEvent(Map<String, Object> companyData, Envelope.Operation operation);
}
