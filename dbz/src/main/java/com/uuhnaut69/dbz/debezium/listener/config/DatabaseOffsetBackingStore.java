package com.uuhnaut69.dbz.debezium.listener.config;

import io.debezium.embedded.EmbeddedEngine;
import lombok.SneakyThrows;
import org.apache.kafka.connect.runtime.WorkerConfig;
import org.apache.kafka.connect.storage.MemoryOffsetBackingStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Instant;
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

    private String fromCheckpointTime;

    private JdbcTemplate jdbcTemplate;

    public DatabaseOffsetBackingStore() {

    }

    @Override
    public void configure(WorkerConfig config) {
        super.configure(config);
        engineName = (String) config.originals().get(EmbeddedEngine.ENGINE_NAME.toString());
        jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(this.configDatasource());

        String initLatestOffset = "CREATE TABLE IF NOT EXISTS latest_offset (id INTEGER NOT NULL AUTO_INCREMENT, offset_key VARCHAR (255) NOT NULL UNIQUE , offset_payload VARCHAR (255), engine_name VARCHAR (255) NOT NULL UNIQUE , created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id)) engine=InnoDB";
        jdbcTemplate.execute(initLatestOffset);

        String initOffsetPositionSeries = "CREATE TABLE IF NOT EXISTS offset_position_series (id INTEGER NOT NULL AUTO_INCREMENT, offset_key VARCHAR (255) NOT NULL, offset_payload VARCHAR (255), engine_name VARCHAR (255) NOT NULL, created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id)) engine=InnoDB";
        jdbcTemplate.execute(initOffsetPositionSeries);

        if (config.originals().containsKey(CustomEmbeddedEngine.CHECK_POINT_MODE)) {
            fromCheckpointTime = (String) config.originals().get(CustomEmbeddedEngine.CHECK_POINT_MODE);
        }
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

            Timestamp createdDate = Timestamp.from(Instant.now());
            String insertOffsetPosition = "INSERT INTO offset_position_series(offset_key, offset_payload, engine_name, created_date) VALUE ( ?, ?, ?, ?)";
            jdbcTemplate.update(insertOffsetPosition, offsetKey, offsetPayload, engineName, createdDate);

            String upsertLatestOffsetPosition = "INSERT INTO latest_offset(offset_key, offset_payload, engine_name, created_date) VALUE ( ?, ?, ?, ?) ON DUPLICATE KEY UPDATE offset_payload = ? , created_date = ?";
            jdbcTemplate.update(upsertLatestOffsetPosition, offsetKey, offsetPayload, engineName, createdDate, offsetPayload, createdDate);
        }
    }

    private void load() {
        data = new HashMap<>();
        List<OffsetPosition> offsetPositions;
        if (!checkStringIsNull(fromCheckpointTime)) {
            offsetPositions = findOffsetInCheckpointMode(engineName, fromCheckpointTime);
            LOGGER.info("Loaded Offset In Manual Mode");
        } else {
            offsetPositions = findOffsetInManualMode(engineName);
            LOGGER.info("Loaded Offset In Checkpoint Mode");
        }
        if (!offsetPositions.isEmpty()) {
            offsetPositions.forEach(offsetPosition -> {
                ByteBuffer key = (offsetPosition.getOffsetKey() != null) ? ByteBuffer.wrap(offsetPosition.getOffsetKey().getBytes()) : null;
                ByteBuffer value = (offsetPosition.getOffsetPayload() != null) ? ByteBuffer.wrap(offsetPosition.getOffsetPayload().getBytes()) : null;
                data.put(key, value);
            });
        }
    }

    private DataSource configDatasource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/demo");
        dataSource.setUsername("root");
        dataSource.setPassword("root1234");
        return dataSource;
    }

    private List<OffsetPosition> findOffsetInManualMode(String engineName) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM latest_offset WHERE engine_name = '");
        sql.append(engineName);
        sql.append("'");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) ->
                new OffsetPosition(rs.getInt("id"), rs.getString("offset_key"), rs.getString("offset_payload"), rs.getString("engine_name"), rs.getTimestamp("created_date"))
        );
    }

    private List<OffsetPosition> findOffsetInCheckpointMode(String engineName, String fromCheckpointTime) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM offset_position_series WHERE engine_name = '");
        sql.append(engineName);
        sql.append("'");
        sql.append(" AND created_date >= '");
        sql.append(fromCheckpointTime);
        sql.append("' ORDER BY created_date ASC LIMIT 1");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) ->
                new OffsetPosition(rs.getInt("id"), rs.getString("offset_key"), rs.getString("offset_payload"), rs.getString("engine_name"), rs.getTimestamp("created_date"))
        );
    }

    private boolean checkStringIsNull(String fromCheckpointTime) {
        return fromCheckpointTime == null || fromCheckpointTime.isEmpty();
    }

}
