package com.uuhnaut69.dbz.debezium.listener.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author uuhnaut
 * @project dbz-monitor
 * @date 5/7/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffsetStore {

    private Integer id;

    private String offsetKey;

    private String offsetPayload;

    private String engineName;

    private Timestamp createdDate;
}
