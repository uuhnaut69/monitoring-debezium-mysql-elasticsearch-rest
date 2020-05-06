package com.uuhnaut69.dbz.debezium.listener.config;

import org.apache.kafka.connect.runtime.WorkerConfig;
import org.apache.kafka.connect.storage.MemoryOffsetBackingStore;
import org.apache.kafka.connect.util.Callback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import static com.uuhnaut69.dbz.debezium.listener.CdcListener.DEFAULT_INSTANCE_ID;

/**
 * @author uuhnaut
 * @project dbz-monitor
 * @date 5/6/20
 */
public class DbOffsetBackingStore extends MemoryOffsetBackingStore {

    private static final Logger LOGGER = Logger.getLogger(DbOffsetBackingStore.class.getName());

    private JdbcTemplate jdbcTemplate;

    public DbOffsetBackingStore() {
    }

    @Override
    public void configure(WorkerConfig config) {
        super.configure(config);
        jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(this.configDatasource());
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS offset_store (instance_id VARCHAR (255) NOT NULL UNIQUE , offset_key TEXT, offset_payload TEXT, PRIMARY KEY (instance_id)) engine=InnoDB");

    }

    @Override
    public Future<Map<ByteBuffer, ByteBuffer>> get(Collection<ByteBuffer> keys, Callback<Map<ByteBuffer, ByteBuffer>> callback) {
        return super.executor.submit(new Callable<Map<ByteBuffer, ByteBuffer>>() {
            @Override
            public Map<ByteBuffer, ByteBuffer> call() throws Exception {
                Map<ByteBuffer, ByteBuffer> result = new HashMap<>();
                for (ByteBuffer key : keys) {
                    result.put(key, data.get(key));
                    LOGGER.warning("Key: " + key.toString());
                    LOGGER.warning("Data:" + data.get(key));
                }
                if (callback != null)
                    callback.onCompletion(null, result);
                return result;
            }
        });
    }

    @Override
    public Future<Void> set(Map<ByteBuffer, ByteBuffer> values, Callback<Void> callback) {
        return super.executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (Map.Entry<ByteBuffer, ByteBuffer> entry : values.entrySet()) {
                    data.put(entry.getKey(), entry.getValue());
                    String offsetKey = new String(entry.getKey().array(), "UTF-8");
                    String offsetContent = new String(entry.getValue().array(), "UTF-8");
                    jdbcTemplate.update("UPDATE offset_store SET offset_key = ? , offset_payload = ? WHERE instance_id = ? ", offsetKey, offsetContent, DEFAULT_INSTANCE_ID);
                }
                save();
                if (callback != null)
                    callback.onCompletion(null, null);
                return null;
            }
        });
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
