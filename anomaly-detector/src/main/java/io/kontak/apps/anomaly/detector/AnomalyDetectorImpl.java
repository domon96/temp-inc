package io.kontak.apps.anomaly.detector;

import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

@Component
public class AnomalyDetectorImpl implements AnomalyDetector {

    private static final Deque<TemperatureReading> LAST_9_MEASUREMENTS = new ArrayDeque<>(9);
    private double sum = 0.0;

    @Override
    public Optional<Anomaly> apply(TemperatureReading temperatureReading) {
        final double temperature = temperatureReading.temperature();
        if (LAST_9_MEASUREMENTS.size() < 9) {
            updateTemperatures(temperatureReading);
            return Optional.empty();
        } else if (LAST_9_MEASUREMENTS.size() == 9) {
            Optional<Anomaly> anomaly = Optional.empty();
            if (Math.abs(getAvg() - temperature) > 5) {
                anomaly = Optional.of(new Anomaly(
                        temperature,
                        temperatureReading.roomId(),
                        temperatureReading.thermometerId(),
                        temperatureReading.timestamp()
                ));
            }
            sum -= LAST_9_MEASUREMENTS.remove().temperature();
            updateTemperatures(temperatureReading);
            return anomaly;
        } else {
            throw new RuntimeException("should never happen");
        }
    }

    private void updateTemperatures(TemperatureReading temperatureReading) {
        LAST_9_MEASUREMENTS.add(temperatureReading);
        sum += temperatureReading.temperature();
    }

    private double getAvg() {
        return sum / LAST_9_MEASUREMENTS.size();
    }
}
