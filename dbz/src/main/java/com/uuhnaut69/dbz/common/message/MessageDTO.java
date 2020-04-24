package com.uuhnaut69.dbz.common.message;

import io.debezium.data.Envelope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private String log;

    private String logType;

    public static MessageDTO getLog(String logType, String tableChange, Map<String, Object> message, Envelope.Operation operation) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("[");
        logBuilder.append(tableChange.toUpperCase());
        logBuilder.append("]");
        logBuilder.append(" Data Changed: ");
        logBuilder.append(message);
        logBuilder.append(" with Operation: ");
        logBuilder.append(operation);
        return MessageDTO.builder().log(logBuilder.toString()).logType(logType).build();
    }

}
