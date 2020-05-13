package com.uuhnaut69.connector.rest;

import com.uuhnaut69.connector.listener.CdcListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    private static final String START_SUCCESSFULLY = "Start successfully";

    private static final String STOP_SUCCESSFULLY = "Stop successfully";

    @PostMapping("/start")
    public String start(@RequestParam(value = "fromCheckpointTime", required = false) Timestamp fromCheckpointTime) {
        cdcListener.start(fromCheckpointTime);
        return START_SUCCESSFULLY;
    }

    @PostMapping("/stop")
    public String stop() throws IOException {
        cdcListener.stop();
        return STOP_SUCCESSFULLY;
    }

}
