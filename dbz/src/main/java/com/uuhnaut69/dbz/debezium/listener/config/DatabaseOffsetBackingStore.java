package com.uuhnaut69.dbz.debezium.listener.config;

import io.debezium.embedded.EmbeddedEngine;
import lombok.SneakyThrows;
import org.apache.kafka.connect.runtime.WorkerConfig;
import org.apache.kafka.connect.storage.MemoryOffsetBackingStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author uuhnaut
 * @project dbz-monitor
 * @date 5/6/20
 */
public class DatabaseOffsetBackingStore extends MemoryOffsetBackingStore {

    private static final Logger LOGGER = Logger.getLogger(DatabaseOffsetBackingStore.class.getName());

    private String engineName;

    private JdbcTemplate jdbcTemplate;

    public DatabaseOffsetBackingStore() {
    }

    @Override
    public void configure(WorkerConfig config) {
        super.configure(config);
        engineName = (String) config.originals().get(EmbeddedEngine.ENGINE_NAME.toString());
        jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(this.configDatasource());
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS offset_store (offset_key VARCHAR (255) NOT NULL UNIQUE, offset_payload VARCHAR (255), engine_name VARCHAR (255) NOT NULL UNIQUE, PRIMARY KEY (offset_key)) engine=InnoDB");
    }

    @Override
    public synchronized void start() {
        super.start();
        LOGGER.info("Starting DatabaseOffsetBackingStore");
        load();
    }

    @Override
    public synchronized void stop() {
        super.stop();
        LOGGER.info("Stopped DatabaseOffsetBackingStore");
    }

    @SneakyThrows
    @Override
    protected void save() {
        Map<ByteBuffer, ByteBuffer> raw = new HashMap<>();
        for (Map.Entry<ByteBuffer, ByteBuffer> mapEntry : data.entrySet()) {
            ByteBuffer key = (mapEntry.getKey() != null) ? mapEntry.getKey() : null;
            ByteBuffer value = (mapEntry.getValue() != null) ? mapEntry.getValue() : null;
            raw.put(key, value);
        }
        for (Map.Entry<ByteBuffer, ByteBuffer> entry : raw.entrySet()) {
            String offsetKey = new String(entry.getKey().array(), UTF_8);
            String offsetPayload = new String(entry.getValue().array(), UTF_8);
            jdbcTemplate.update("INSERT INTO offset_store(offset_key, offset_payload, engine_name) VALUE ( ?, ?, ?) ON DUPLICATE KEY UPDATE offset_payload = ?", offsetKey, offsetPayload, engineName, offsetPayload);
        }
    }

    private void load() {
        List<OffsetStore> offsetStores = findOffsetStoreByInstanceId(engineName);
        if (!offsetStores.isEmpty()) {
            data = new HashMap<>();
            offsetStores.forEach(offsetStore -> {
                ByteBuffer key = (offsetStore.getOffsetKey() != null) ? ByteBuffer.wrap(offsetStore.getOffsetKey().getBytes()) : null;
                ByteBuffer value = (offsetStore.getOffsetPayload() != null) ? ByteBuffer.wrap(offsetStore.getOffsetPayload().getBytes()) : null;
                data.put(key, value);
            });
        }
    }

    private List<OffsetStore> findOffsetStoreByInstanceId(String instanceId) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM offset_store WHERE engine_name = '");
        sqlBuilder.append(instanceId);
        sqlBuilder.append("'");
        return jdbcTemplate.query(sqlBuilder.toString(), (rs, rowNum) ->
                new OffsetStore(rs.getString("offset_key"), rs.getString("offset_payload"), rs.getString("engine_name"))
        );
    }

    private DataSource configDatasource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/demo");
        dataSource.setUsername("root");
        dataSource.setPassword("root1234");
        return dataSource;
    }
}
