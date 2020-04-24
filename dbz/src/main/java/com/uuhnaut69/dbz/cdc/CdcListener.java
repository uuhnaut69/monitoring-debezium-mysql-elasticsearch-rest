package com.uuhnaut69.dbz.cdc;

import com.uuhnaut69.dbz.common.message.MessageConstant;
import com.uuhnaut69.dbz.common.message.MessageDTO;
import com.uuhnaut69.dbz.elasticsearch.service.CompanyEsService;
import com.uuhnaut69.dbz.elasticsearch.service.JobEsService;
import io.debezium.config.Configuration;
import io.debezium.connector.mysql.MySqlConnectorConfig;
import io.debezium.data.Envelope;
import io.debezium.embedded.EmbeddedEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.*;
import static java.util.stream.Collectors.toMap;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Slf4j
@Component
public class CdcListener {

    private static final String APP_CONNECTOR_1 = "mysql-connector-1";

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final EmbeddedEngine engine;

    private final CompanyEsService companyEsService;

    private final JobEsService jobEsService;

    private final RabbitTemplate rabbitTemplate;

    public CdcListener(CompanyEsService companyEsService, JobEsService jobEsService, RabbitTemplate rabbitTemplate) {
        this.engine = EmbeddedEngine.create().using(this.createConnector()).notifying(this::handleEvent).build();
        this.companyEsService = companyEsService;
        this.jobEsService = jobEsService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void start() {
        this.executor.execute(engine);
    }

    public void stop() {
        if (this.engine != null) {
            this.engine.stop();
        }
    }


    private void handleEvent(SourceRecord sourceRecord) {

        /*
         Omit schema change events
         */
        if (sourceRecord.topic().equals(APP_CONNECTOR_1)) {
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

    private Configuration createConnector() {
        return Configuration.create()
                .with(EmbeddedEngine.CONNECTOR_CLASS, "io.debezium.connector.mysql.MySqlConnector")
                .with(EmbeddedEngine.OFFSET_STORAGE, "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with(EmbeddedEngine.OFFSET_STORAGE_FILE_FILENAME, "/Users/uuhnaut/Documents/data/dboffset.dat")
                .with(EmbeddedEngine.OFFSET_FLUSH_INTERVAL_MS, 0)
                .with(EmbeddedEngine.ENGINE_NAME, APP_CONNECTOR_1)
                .with(MySqlConnectorConfig.SERVER_NAME, APP_CONNECTOR_1)
                .with(MySqlConnectorConfig.HOSTNAME, "localhost")
                .with(MySqlConnectorConfig.PORT, 3306)
                .with(MySqlConnectorConfig.SERVER_ID, 223344)
                .with(MySqlConnectorConfig.USER, "root")
                .with(MySqlConnectorConfig.PASSWORD, "root1234")
                .with("database.dbname", "demo")
                .with(MySqlConnectorConfig.DATABASE_HISTORY, "io.debezium.relational.history.FileDatabaseHistory")
                .with("database.history.file.filename", "/Users/uuhnaut/Documents/data/dbhistory.dat")
                .with(MySqlConnectorConfig.TABLE_WHITELIST, "demo.company,demo.job")
                .with("schemas.enable", false)
                .with("slot.name", "demo")
                .build();
    }
}
