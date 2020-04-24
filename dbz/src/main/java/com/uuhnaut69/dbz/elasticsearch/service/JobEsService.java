package com.uuhnaut69.dbz.elasticsearch.service;

import io.debezium.data.Envelope;

import java.util.Map;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
public interface JobEsService {

    void handleEvent(Map<String, Object> jobData, Envelope.Operation operation);
}
