package io.kontak.apps.anomaly.detector;

import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import io.kontak.apps.anomaly.detector.storage.service.AnomalyService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
class AnomalyDetectorImplTest {

    @Mock
    private final AnomalyService anomalyService = Mockito.mock(AnomalyService.class);
    private final AnomalyDetector anomalyDetector = new AnomalyDetectorImpl(anomalyService);

    @BeforeEach
    public void clearCache() {
        anomalyDetector.clearCache();
    }

    @Test
    void shouldReturnNoAnomalyWhenLessThan10Readings() {
        assertEquals(Optional.empty(), anomalyDetector.apply(List.of(
                reading(1), reading(1), reading(100)
        )));
        anomalyDetector.clearCache();
        assertEquals(Optional.empty(), anomalyDetector.apply(List.of(
                reading(1), reading(1), reading(1),
                reading(1), reading(1), reading(1),
                reading(1), reading(1), reading(100)
        )));

        verifyNoInteractions(anomalyService);
    }

    @Test
    void shouldReturnNoAnomalyWhenReadingsInAverageRange() {
        assertEquals(Optional.empty(), anomalyDetector.apply(getNumberOfReadingsWithAvg(10, 10)));
        anomalyDetector.clearCache();
        assertEquals(Optional.empty(), anomalyDetector.apply(getNumberOfReadingsWithAvg(20, 20)));
        anomalyDetector.clearCache();
        assertEquals(Optional.empty(), anomalyDetector.apply(Stream.concat(
                getNumberOfReadingsWithAvg(10, 10).stream(),
                getNumberOfReadingsWithAvg(10, 13).stream()
        ).toList()));
        anomalyDetector.clearCache();
        assertEquals(Optional.empty(), anomalyDetector.apply(Stream.concat(
                getNumberOfReadingsWithAvg(10, 10).stream(),
                getNumberOfReadingsWithAvg(10, 7).stream()
        ).toList()));
        anomalyDetector.clearCache();
        assertEquals(Optional.empty(), anomalyDetector.apply(Stream.concat(
                getNumberOfReadingsWithAvg(10, 10).stream(),
                getNumberOfReadingsWithAvg(10, 15).stream()
        ).toList()));
        anomalyDetector.clearCache();
        assertEquals(Optional.empty(), anomalyDetector.apply(Stream.concat(
                getNumberOfReadingsWithAvg(10, 10).stream(),
                getNumberOfReadingsWithAvg(10, 5).stream()
        ).toList()));

        verifyNoInteractions(anomalyService);
    }

    @Test
    void shouldReturnAnomalyWhenReadingsAboveRange() {
        assertEquals(Optional.of(anomaly(20)),
                anomalyDetector.apply(Stream.concat(
                        getNumberOfReadingsWithAvg(10, 10).stream(),
                        getNumberOfReadingsWithAvg(1, 20).stream()
                ).toList()));
        anomalyDetector.clearCache();
        assertEquals(Optional.of(anomaly(2)),
                anomalyDetector.apply(Stream.concat(
                        getNumberOfReadingsWithAvg(10, 10).stream(),
                        getNumberOfReadingsWithAvg(1, 2).stream()
                ).toList()));

        verify(anomalyService, times(1)).saveAnomaly(anomaly(20));
        verify(anomalyService, times(1)).saveAnomaly(anomaly(2));
        verifyNoMoreInteractions(anomalyService);
    }

    @Test
    void shouldReturnNoAnomalyWhenReadingsInAverageRangeSequential() {
        assertEquals(Optional.empty(), anomalyDetector.apply(getNumberOfReadingsWithAvg(10, 10)));
        assertEquals(Optional.empty(), anomalyDetector.apply(List.of(reading(15))));
        anomalyDetector.clearCache();
        assertEquals(Optional.empty(), anomalyDetector.apply(getNumberOfReadingsWithAvg(10, 10)));
        assertEquals(Optional.empty(), anomalyDetector.apply(List.of(reading(5))));

        verifyNoInteractions(anomalyService);
    }

    @Test
    void shouldReturnAnomalyWhenReadingsAboveAverageRangeSequential() {
        assertEquals(Optional.empty(), anomalyDetector.apply(getNumberOfReadingsWithAvg(10, 10)));
        assertEquals(Optional.empty(), anomalyDetector.apply(List.of(reading(15))));
        // new average > 10
        assertEquals(Optional.of(anomaly(5)), anomalyDetector.apply(List.of(reading(5))));
        anomalyDetector.clearCache();
        assertEquals(Optional.empty(), anomalyDetector.apply(getNumberOfReadingsWithAvg(10, 10)));
        assertEquals(Optional.empty(), anomalyDetector.apply(List.of(reading(5))));
        // new average < 10
        assertEquals(Optional.of(anomaly(15)), anomalyDetector.apply(List.of(reading(15))));

        verify(anomalyService, times(1)).saveAnomaly(anomaly(5));
        verify(anomalyService, times(1)).saveAnomaly(anomaly(15));
        verifyNoMoreInteractions(anomalyService);
    }

    private TemperatureReading reading(int temp) {
        return new TemperatureReading(temp, "", "", null);
    }

    private Anomaly anomaly(int temp) {
        return new Anomaly(temp, "", "", null);
    }

    private List<TemperatureReading> getNumberOfReadingsWithAvg(int count, int avg) {
        return Stream.generate(() -> reading(avg))
                .limit(count)
                .collect(Collectors.toList());
    }
}