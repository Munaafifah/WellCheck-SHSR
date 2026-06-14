package com.SmartHealthRemoteSystem.SHSR.ReadSensorData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Scanner;

@Profile("local")
@Component
public class SerialReader {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        new Thread(this::readSerial, "Serial-COM4").start();
    }

    /* ---------------------------------------------------------------------- */
    private void readSerial() {
        SerialPort port = SerialPort.getCommPort("COM4");
        port.setBaudRate(9600);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        if (!port.openPort()) {
            System.out.println("❌  Could NOT open COM4");
            return;
        }
        System.out.println("✅  COM4 opened → listening …");

        try (InputStream in = port.getInputStream();
             Scanner sc = new Scanner(in, StandardCharsets.UTF_8)) {

           while (sc.hasNextLine()) {
    String raw = sc.nextLine().trim();

    System.out.println("Received line: " + raw); // <--- Add this

    if (raw.length() < 3 || raw.charAt(0) != '{') {
        System.out.println("⚠  Skipped: " + raw);
        continue;
    }
    try {
        SensorReading r = mapper.readValue(raw, SensorReading.class);
        upsertSensor("SENSOR_LIWEI_01", r);
    } catch (Exception ex) {
        System.err.println("❌  JSON parse error: " + raw);
        ex.printStackTrace();
    }
}


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            port.closePort();
            System.out.println("⚠  COM4 closed");
        }
    }

    /* ------------------------------------------------------------------ */
    private void upsertSensor(String sensorId, SensorReading r) {

        SensorData data = sensorDataRepository.get(sensorId);

        if (data == null) {
            data = new SensorData();
            data.setSensorDataId(sensorId);
        }
        data.setHeart_Rate(r.heartRate);
        data.setEcgReading(r.ecgValue);
        data.setBodyTemperature(r.temperature);
        data.setOxygenReading(r.oxygenReading);
        data.setTimestamp(Instant.now());

        if (sensorDataRepository.get(sensorId) == null) {
            sensorDataRepository.save(data);
            System.out.println("✅  Inserted new SensorData");
        } else {
            sensorDataRepository.update(data);
            System.out.println("✅  Updated SensorData");
        }

        /* ---- append history ---- */
        HistorySensorData h =
                new HistorySensorData(r.heartRate, r.temperature,
                                      r.ecgValue, r.oxygenReading);
        sensorDataRepository.addToHistory(sensorId, h);
    }

    /* ------------------------------------------------------------------ */
    public static class SensorReading {
        public int    heartRate;
        public double ecgValue;
        public double temperature;
        public int    oxygenReading;
    }
}
