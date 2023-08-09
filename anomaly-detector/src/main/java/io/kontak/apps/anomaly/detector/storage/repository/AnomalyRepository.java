package io.kontak.apps.anomaly.detector.storage.repository;

import io.kontak.apps.anomaly.detector.storage.model.AnomalyDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnomalyRepository extends JpaRepository<AnomalyDao, Long> {

    List<AnomalyDao> findAllByThermometerId(String thermometerId);

    List<AnomalyDao> findAllByRoomId(String roomId);
}
