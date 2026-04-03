// package com.SmartHealthRemoteSystem.SHSR.SerialReader;

// import com.SmartHealthRemoteSystem.SHSR.Sensor.Model.TemperatureSensorData;
// import com.SmartHealthRemoteSystem.SHSR.Sensor.Repository.TemperatureSensorRepository;
// import com.fazecast.jSerialComm.SerialPort;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.springframework.stereotype.Component;

// import javax.annotation.PostConstruct;
// import java.io.InputStream;
// import java.nio.charset.StandardCharsets;
// import java.util.Scanner;

// // @Component
// public class TemperatureSerialReader {

//     private final TemperatureSensorRepository repository;
//     private final ObjectMapper objectMapper = new ObjectMapper();

//     public TemperatureSerialReader(TemperatureSensorRepository repository) {
//         this.repository = repository;
//     }

//     @PostConstruct
//     public void init() {
//         new Thread(this::readSerial).start();
//     }

//     private void readSerial() {
//         // Change COM port accordingly
//         SerialPort port = SerialPort.getCommPort("COM4");
//         port.setBaudRate(9600);

//         if (port.openPort()) {
//             System.out.println("Serial port opened.");
//             try (InputStream in = port.getInputStream(); Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
//                 while (scanner.hasNextLine()) {
//                     String line = scanner.nextLine();
//                     processLine(line);
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//             } finally {
//                 port.closePort();
//                 System.out.println("Serial port closed.");
//             }
//         } else {
//             System.out.println("Failed to open serial port.");
//         }
//     }

//     private void processLine(String line) {
//         try {
//             JsonNode jsonNode = objectMapper.readTree(line);
//             double ambient = jsonNode.get("ambient").asDouble();
//             double objectTemp = jsonNode.get("object").asDouble();

//             TemperatureSensorData data = new TemperatureSensorData(ambient, objectTemp);
//             repository.save(data);
//             System.out.println("Saved: " + data);

//         } catch (Exception e) {
//             System.err.println("Error parsing line: " + line);
//             e.printStackTrace();
//         }
//     }
// }
