package io.kontak.apps.anomaly.detector;

import io.kontak.apps.anomaly.detector.storage.service.AnomalyService;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

@Component
public class AnomalyDetectorImpl implements AnomalyDetector {

    private static final Deque<TemperatureReading> LAST_9_MEASUREMENTS = new ArrayDeque<>(9);
    private final AnomalyService anomalyService;
    private double sum = 0.0;

    public AnomalyDetectorImpl(AnomalyService anomalyService) {
        this.anomalyService = anomalyService;
    }

    @Override
    public Optional<Anomaly> apply(List<TemperatureReading> temperatureReadings) {
        for (var temperatureReading : temperatureReadings) {
            final double temperature = temperatureReading.temperature();
            if (LAST_9_MEASUREMENTS.size() < 9) {
                updateTemperatures(temperatureReading);
            } else if (LAST_9_MEASUREMENTS.size() == 9) {
                boolean shouldReturnAnomaly = false;
                if (Math.abs(getAvg() - temperature) > 5) {
                    shouldReturnAnomaly = true;
                }
                sum -= LAST_9_MEASUREMENTS.remove().temperature();
                updateTemperatures(temperatureReading);
                if (shouldReturnAnomaly) {
                    final Anomaly anomaly = new Anomaly(
                            temperature,
                            temperatureReading.roomId(),
                            temperatureReading.thermometerId(),
                            temperatureReading.timestamp()
                    );
                    anomalyService.saveAnomaly(anomaly);
                    return Optional.of(anomaly);
                }
            } else {
                throw new RuntimeException("should never happen");
            }
        }
        return Optional.empty();
    }

    private void updateTemperatures(TemperatureReading temperatureReading) {
        LAST_9_MEASUREMENTS.add(temperatureReading);
        sum += temperatureReading.temperature();
    }

    private double getAvg() {
        return sum / LAST_9_MEASUREMENTS.size();
    }

    @Override
    public void clearCache() {
        LAST_9_MEASUREMENTS.clear();
        sum = 0.0;
    }
}
