package com.uuhnaut69.dbz.debezium.listener.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author uuhnaut
 * @project dbz-monitor
 * @date 5/7/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffsetStore {

    private String offsetKey;

    private String offsetPayload;

    private String instanceId;
}
