package io.kontak.apps.anomaly.detector.storage.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "anomaly")
public class AnomalyDao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    Double temperature;
    String roomId;
    String thermometerId;
    Instant timestamp;

    public AnomalyDao(Double temperature, String roomId, String thermometerId, Instant timestamp) {
        this.temperature = temperature;
        this.roomId = roomId;
        this.thermometerId = thermometerId;
        this.timestamp = timestamp;
    }

    public AnomalyDao() {
    }

    public Double getTemperature() {
        return temperature;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getThermometerId() {
        return thermometerId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
