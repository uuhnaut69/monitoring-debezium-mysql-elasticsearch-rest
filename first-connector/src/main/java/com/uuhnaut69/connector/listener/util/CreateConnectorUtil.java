package com.uuhnaut69.connector.listener.util;

import com.uuhnaut69.connector.listener.config.CustomEmbeddedEngine;
import io.debezium.connector.mysql.MySqlConnectorConfig;
import io.debezium.embedded.EmbeddedEngine;

import java.sql.Timestamp;
import java.util.Properties;

/**
 * @author uuhnaut
 * @project dbz-monitor
 * @date 5/8/20
 */
public final class CreateConnectorUtil {

    public static final String APP_CONNECTOR = "mysql-connector";

    public static Properties createConnector(Timestamp fromCheckpointTime) {
        Properties properties = new Properties();
        properties.setProperty(EmbeddedEngine.CONNECTOR_CLASS.toString(), "io.debezium.connector.mysql.MySqlConnector");
        properties.setProperty(EmbeddedEngine.OFFSET_STORAGE.toString(), "com.uuhnaut69.connector.listener.config.DatabaseOffsetBackingStore");
        properties.setProperty(EmbeddedEngine.ENGINE_NAME.toString(), APP_CONNECTOR);
        properties.setProperty(MySqlConnectorConfig.SERVER_NAME.toString(), APP_CONNECTOR);
        properties.setProperty(MySqlConnectorConfig.HOSTNAME.toString(), "localhost");
        properties.setProperty(MySqlConnectorConfig.PORT.toString(), "3306");
        properties.setProperty(MySqlConnectorConfig.SERVER_ID.toString(), "223344");
        properties.setProperty(MySqlConnectorConfig.USER.toString(), "root");
        properties.setProperty(MySqlConnectorConfig.PASSWORD.toString(), "root1234");
        properties.setProperty("database.dbname", "demo");
        properties.setProperty(MySqlConnectorConfig.DATABASE_HISTORY.toString(), "io.debezium.relational.history.FileDatabaseHistory");
        properties.setProperty("database.history.file.filename", "/Users/uuhnaut/Documents/data/dbhistory.dat");
        properties.setProperty(MySqlConnectorConfig.TABLE_WHITELIST.toString(), "demo.company,demo.job");
        properties.setProperty("schemas.enable", "false");
        properties.setProperty("slot.name", "demo");
        if (fromCheckpointTime != null) {
            properties.setProperty(CustomEmbeddedEngine.CHECK_POINT_MODE, fromCheckpointTime.toString());
        }
        return properties;
    }

    private CreateConnectorUtil() {
    }
}
