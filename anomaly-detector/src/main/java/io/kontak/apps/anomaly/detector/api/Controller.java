/*
 * Copyright (C) Motorola Solutions, INC.
 * All Rights Reserved.
 */

package io.kontak.apps.anomaly.detector.api;

import io.kontak.apps.anomaly.detector.storage.service.AnomalyService;
import io.kontak.apps.event.Anomaly;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
public class Controller {

    private final AnomalyService anomalyService;

    public Controller(AnomalyService anomalyService) {
        this.anomalyService = anomalyService;
    }

    @GetMapping("/anomalies/thermometer/{id}")
    public List<Anomaly> getAnomaliesByThermometerId(@PathVariable String id) {
        return anomalyService.getAnomaliesByThermometerId(id);
    }

    @GetMapping("/anomalies/room/{id}")
    public List<Anomaly> getAnomaliesByRoomId(@PathVariable String id) {
        return anomalyService.getAnomaliesByRoomId(id);
    }

    @GetMapping("/thermometers/{threshold}")
    public List<String> getThermometersWithAnomaliesHigherThan(@PathVariable int threshold) {
        return anomalyService.getThermometersWithAmountOfAnomaliesHigherThanThreshold(threshold);
    }
}
