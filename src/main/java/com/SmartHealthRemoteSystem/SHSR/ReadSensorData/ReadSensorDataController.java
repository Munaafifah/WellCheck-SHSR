package com.SmartHealthRemoteSystem.SHSR.ReadSensorData;

import com.SmartHealthRemoteSystem.SHSR.Service.SensorDataService;
import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.HistorySensorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Sensor-data")
public class ReadSensorDataController {

    @Autowired
    private SensorDataService sensorDataService;

    // ✅ Save full sensor data (used for first time saving or fully new reading)
    @PostMapping("/save")
    public String saveSensor(@RequestBody SensorData sensorData) {
        return sensorDataService.saveSensorData(sensorData);
    }

    // ✅ Get sensor data by sensorDataId
    @GetMapping("/get-sensor-data/{sensorDataId}")
    public SensorData getSensor(@PathVariable String sensorDataId) {
        return sensorDataService.getSensorById(sensorDataId);
    }

    // ✅ Update existing latest sensor reading
    @PutMapping("/update")
    public boolean updateSensor(@RequestBody SensorData sensorData) {
        return sensorDataService.updateSensorData(sensorData);
    }

    // ✅ Delete sensor data completely (optional)
    @DeleteMapping("/delete/{sensorDataId}")
    public boolean deleteSensor(@PathVariable String sensorDataId) {
        return sensorDataService.deleteSensorData(sensorDataId);
    }

    // ✅ Get all sensor data (optional, for admin/debug purposes)
    @GetMapping("/all")
    public List<SensorData> getAllSensors() {
        return sensorDataService.getAllSensorData();
    }

    // ✅ Append historical data into the nested list
    @PostMapping("/add-history/{sensorDataId}")
    public boolean addHistory(@PathVariable String sensorDataId, @RequestBody HistorySensorData historyData) {
        return sensorDataService.appendHistory(sensorDataId, historyData);
    }


    
}
