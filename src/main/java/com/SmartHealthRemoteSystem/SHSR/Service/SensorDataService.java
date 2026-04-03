package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.HistorySensorData;
import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class SensorDataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    // ✅ Save new sensor data (for real-time update from serial etc)
    public String saveSensorData(SensorData sensorData) {
        sensorData.setTimestamp(Instant.now());
        return sensorDataRepository.save(sensorData);
    }

    // ✅ Get all sensor data (optional - for admin side if needed)
    public List<SensorData> getAllSensorData() {
        return sensorDataRepository.getAll();
    }

    // ✅ Get specific sensor data by sensorDataId
    public SensorData getSensorById(String sensorId) {
        return sensorDataRepository.get(sensorId);
    }

    // ✅ Update latest reading
    public boolean updateSensorData(SensorData updatedSensorData) {
        SensorData existing = getSensorById(updatedSensorData.getSensorDataId());
        if (existing != null) {
            updatedSensorData.setTimestamp(Instant.now());
            sensorDataRepository.update(updatedSensorData);
            return true;
        }
        return false;
    }

    // ✅ Delete sensor data
    public boolean deleteSensorData(String sensorId) {
        sensorDataRepository.delete(sensorId);
        return true;
    }

    // ✅ Get most recent sensor data (optional for auto-monitoring use cases)
    public Optional<SensorData> getMostRecentSensor() {
        List<SensorData> sensorDataList = getAllSensorData();
        return sensorDataList.stream()
                .max(Comparator.comparing(SensorData::getTimestamp));
    }

    // ✅ Append history into nested history array
    public boolean appendHistory(String sensorDataId, HistorySensorData historyData) {
        return sensorDataRepository.addToHistory(sensorDataId, historyData);
    }
}
