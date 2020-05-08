package com.uuhnaut69.dbz.debezium.listener;

import com.uuhnaut69.dbz.common.message.MessageConstant;
import com.uuhnaut69.dbz.common.message.MessageDTO;
import com.uuhnaut69.dbz.debezium.listener.config.CreateConnectorUtil;
import com.uuhnaut69.dbz.elasticsearch.service.CompanyEsService;
import com.uuhnaut69.dbz.elasticsearch.service.JobEsService;
import io.debezium.data.Envelope;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.uuhnaut69.dbz.debezium.listener.config.CreateConnectorUtil.APP_CONNECTOR;
import static io.debezium.data.Envelope.FieldName.*;
import static java.util.stream.Collectors.toMap;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Slf4j
@Component
public class CdcListener {

    private final Executor executor = Executors.newSingleThreadExecutor();

    private DebeziumEngine<SourceRecord> engine;

    private final CompanyEsService companyEsService;

    private final JobEsService jobEsService;

    private final RabbitTemplate rabbitTemplate;

    public CdcListener(CompanyEsService companyEsService, JobEsService jobEsService, RabbitTemplate rabbitTemplate) {
        this.companyEsService = companyEsService;
        this.jobEsService = jobEsService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void start(Timestamp fromCheckpointTime) {
        this.engine = DebeziumEngine.create(Connect.class).using(CreateConnectorUtil.createConnector(fromCheckpointTime)).notifying(this::handleEvent).build();
        this.executor.execute(engine);
    }

    @PreDestroy
    public void stop() throws IOException {
        if (this.engine != null) {
            this.engine.close();
        }
    }


    private void handleEvent(SourceRecord sourceRecord) {

        /*
         Omit schema change events
         */
        if (sourceRecord.topic().equals(APP_CONNECTOR)) {
            return;
        }

        Struct sourceRecordValue = (Struct) sourceRecord.value();
        if (sourceRecordValue != null) {
            Envelope.Operation operation = Envelope.Operation.forCode((String) sourceRecordValue.get(OPERATION));
            Map<String, Object> message;
            String record = AFTER;

            if (operation == Envelope.Operation.DELETE) {
                record = BEFORE;
            }

            Struct struct = (Struct) sourceRecordValue.get(record);
            message = struct.schema().fields().stream().map(Field::name)
                    .filter(fieldName -> struct.get(fieldName) != null)
                    .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                    .collect(toMap(Pair::getKey, Pair::getValue));

            Struct source = (Struct) sourceRecordValue.get(SOURCE);
            String tableChange = source.get("table").toString();
            switch (tableChange) {
                case "company":
                    this.companyEsService.handleEvent(message, operation);
                    this.rabbitTemplate.convertAndSend("/topic/log-info", MessageDTO.getLog(MessageConstant.INFO, tableChange, message, operation));
                    break;
                case "job":
                    this.jobEsService.handleEvent(message, operation);
                    this.rabbitTemplate.convertAndSend("/topic/log-info", MessageDTO.getLog(MessageConstant.INFO, tableChange, message, operation));
                    break;
                default:
                    this.rabbitTemplate.convertAndSend("/topic/log-error", MessageDTO.getLog(MessageConstant.ERROR, tableChange, message, operation));
                    throw new IllegalStateException("Unexpected value: " + tableChange);
            }
        }
    }
}
