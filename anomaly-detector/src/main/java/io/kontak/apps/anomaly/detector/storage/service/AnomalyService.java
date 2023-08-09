package io.kontak.apps.anomaly.detector.storage.service;

import io.kontak.apps.anomaly.detector.storage.model.AnomalyDao;
import io.kontak.apps.anomaly.detector.storage.repository.AnomalyRepository;
import io.kontak.apps.event.Anomaly;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnomalyService {

    private final AnomalyRepository anomalyRepository;

    public AnomalyService(AnomalyRepository anomalyRepository) {
        this.anomalyRepository = anomalyRepository;
    }

    public void saveAnomaly(Anomaly anomaly) {
        anomalyRepository.save(new AnomalyDao(
                anomaly.temperature(),
                anomaly.roomId(),
                anomaly.thermometerId(),
                anomaly.timestamp()
        ));
    }

    public List<Anomaly> getAnomaliesByThermometerId(String thermometerId) {
        return anomalyRepository.findAllByThermometerId(thermometerId).stream()
                .map(anomalyDao -> new Anomaly(
                        anomalyDao.getTemperature(),
                        anomalyDao.getRoomId(),
                        anomalyDao.getThermometerId(),
                        anomalyDao.getTimestamp()
                )).toList();
    }
}
