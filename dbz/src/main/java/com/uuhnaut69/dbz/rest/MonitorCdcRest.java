package com.uuhnaut69.dbz.rest;

import com.uuhnaut69.dbz.cdc.CdcListener;
import com.uuhnaut69.dbz.common.message.MessageConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public String start() {
        cdcListener.start();
        return MessageConstant.START_SUCCESSFULLY;
    }

    @PostMapping("/stop")
    public String stop() {
        cdcListener.stop();
        return MessageConstant.STOP_SUCCESSFULLY;
    }

    @PostMapping("/reset")
    public String resetSync() throws IOException {
        cdcListener.stop();
        try {
            Files.deleteIfExists(Paths.get(MessageConstant.OFFSET_FILE_DIRECTORY));
            Files.deleteIfExists(Paths.get(MessageConstant.HISTORY_SCHEMA_FILE_DIRECTORY));
        } catch (NoSuchFileException e) {
            log.error("No such file/directory exists");
        } finally {
            Path offsetPath = Paths.get(MessageConstant.OFFSET_FILE_DIRECTORY);
            Path historyPath = Paths.get(MessageConstant.HISTORY_SCHEMA_FILE_DIRECTORY);
            Files.createFile(offsetPath);
            Files.createFile(historyPath);
        }
        cdcListener.start();
        return MessageConstant.RESET_SUCCESSFULLY;
    }
}
