package com.uuhnaut69.dbz.rest;

import com.uuhnaut69.dbz.common.message.MessageConstant;
import com.uuhnaut69.dbz.debezium.listener.CdcListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Slf4j
@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class MonitorCdcRest {

    private final CdcListener cdcListener;

    @PostMapping("/start")
    public String start(@RequestParam(value = "fromCheckpointTime", required = false) Timestamp fromCheckpointTime) {
        cdcListener.start(fromCheckpointTime);
        return MessageConstant.START_SUCCESSFULLY;
    }

    @PostMapping("/stop")
    public String stop() throws IOException {
        cdcListener.stop();
        return MessageConstant.STOP_SUCCESSFULLY;
    }

    @PostMapping("/reset")
    public String resetSync(@RequestParam(value = "fromCheckpointTime", required = false) String fromCheckpointTime) throws IOException {
//        cdcListener.stop();
//        try {
//            Files.deleteIfExists(Paths.get(MessageConstant.HISTORY_SCHEMA_FILE_DIRECTORY));
//        } catch (NoSuchFileException e) {
//            log.error("No such file/directory exists");
//        } finally {
//            Path historyPath = Paths.get(MessageConstant.HISTORY_SCHEMA_FILE_DIRECTORY);
//            Files.createFile(historyPath);
//        }
//        cdcListener.start(fromCheckpointTime);
        return MessageConstant.RESET_SUCCESSFULLY;
    }
}
