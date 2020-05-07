package com.uuhnaut69.dbz.debezium.listener.config;

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

import static com.uuhnaut69.dbz.debezium.listener.CdcListener.DEFAULT_INSTANCE_ID;

/**
 * @author uuhnaut
 * @project dbz-monitor
 * @date 5/6/20
 */
public class DatabaseOffsetBackingStore extends MemoryOffsetBackingStore {

    private static final Logger LOGGER = Logger.getLogger(DatabaseOffsetBackingStore.class.getName());

    private JdbcTemplate jdbcTemplate;

    public DatabaseOffsetBackingStore() {
    }

    @Override
    public void configure(WorkerConfig config) {
        super.configure(config);
        jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(this.configDatasource());
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS offset_store (offset_key VARCHAR (255) NOT NULL UNIQUE, offset_payload VARCHAR (255), instance_id VARCHAR (255), PRIMARY KEY (offset_key)) engine=InnoDB");
    }

    @Override
    public void start() {
        super.start();
        LOGGER.info("Load offset store from database");
        load();
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
            String offsetKey = new String(entry.getKey().array(), "UTF-8");
            String offsetPayload = new String(entry.getValue().array(), "UTF-8");
            jdbcTemplate.update("INSERT INTO offset_store(offset_key, offset_payload, instance_id) VALUE ( ?, ?, ?) ON DUPLICATE KEY UPDATE offset_payload = ?", offsetKey, offsetPayload, DEFAULT_INSTANCE_ID, offsetPayload);
        }
    }

    private void load() {
        List<OffsetStore> offsetStores = findOffsetStoreByInstanceId(DEFAULT_INSTANCE_ID);
        data = new HashMap<>();
        offsetStores.stream().forEach(offsetStore -> {
            ByteBuffer key = (offsetStore.getOffsetKey() != null) ? ByteBuffer.wrap(offsetStore.getOffsetKey().getBytes()) : null;
            ByteBuffer value = (offsetStore.getOffsetPayload() != null) ? ByteBuffer.wrap(offsetStore.getOffsetPayload().getBytes()) : null;
            data.put(key, value);
        });
    }

    private List<OffsetStore> findOffsetStoreByInstanceId(String instanceId) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM offset_store WHERE instance_id = '");
        sqlBuilder.append(DEFAULT_INSTANCE_ID);
        sqlBuilder.append("'");
        return jdbcTemplate.query(sqlBuilder.toString(), (rs, rowNum) ->
                new OffsetStore(rs.getString("offset_key"), rs.getString("offset_payload"), rs.getString("instance_id"))
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
