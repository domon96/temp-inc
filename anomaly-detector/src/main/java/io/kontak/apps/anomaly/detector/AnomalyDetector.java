package io.kontak.apps.anomaly.detector;

import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;

import java.util.Optional;
import java.util.function.Function;

public interface AnomalyDetector extends Function<TemperatureReading, Optional<Anomaly>> {

}
