package com.SmartHealthRemoteSystem.SHSR.ReadSensorData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${active.sensor.id}")
    private String activeSensorId;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        new Thread(this::readSerial, "Serial-COM5").start();
    }

    /* ---------------------------------------------------------------------- */
    private void readSerial() {
        SerialPort port = SerialPort.getCommPort("COM5");
        port.setBaudRate(9600);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        if (!port.openPort()) {
            System.out.println("❌  Could NOT open COM5");
            return;
        }
        System.out.println("✅  COM5 opened → listening …");

        try (InputStream in = port.getInputStream();
             Scanner sc = new Scanner(in, StandardCharsets.UTF_8)) {

            while (sc.hasNextLine()) {
                String raw = sc.nextLine().trim();

                System.out.println("Received line: " + raw);

                if (raw.length() < 3 || raw.charAt(0) != '{') {
                    System.out.println("⚠  Skipped: " + raw);
                    continue;
                }
                try {
                    SensorReading r = mapper.readValue(raw, SensorReading.class);

                    // ✅ Defense in depth: only accept the reading if all 4
                    // values look real. The Arduino already gates this before
                    // sending, but the server should never trust the wire blindly.
                    if (r.heartRate <= 0 || r.temperature <= 0
                            || r.oxygenReading <= 0 || r.ecgValue <= 0) {
                        System.out.println("⚠  Incomplete reading, skipped: " + raw);
                        continue;
                    }

                    upsertSensor(activeSensorId, r);
                } catch (Exception ex) {
                    System.err.println("❌  JSON parse error: " + raw);
                    ex.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            port.closePort();
            System.out.println("⚠  COM5 closed");
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