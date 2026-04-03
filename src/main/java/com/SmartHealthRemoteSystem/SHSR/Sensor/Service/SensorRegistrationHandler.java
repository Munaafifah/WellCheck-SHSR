package com.SmartHealthRemoteSystem.SHSR.Sensor.Service;

import com.SmartHealthRemoteSystem.SHSR.Sensor.Model.Sensor;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Model.PatientSensorStatus;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Repository.SensorRepository;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Sensor.RegistrationResult;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class SensorRegistrationHandler {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;


    /**
     * Register sensor using uniqueKey
     */
    public RegistrationResult registerSensor(String patientID, String patientName, String sensorID, String uniqueKey) {
        Sensor keySensor = sensorRepository.findByUniqueKey(uniqueKey);

        if (keySensor == null) {
            return new RegistrationResult(false, "Invalid unique key.");
        }

        if (keySensor.getSensorDataId() != null && !keySensor.getSensorDataId().isEmpty()) {
            return new RegistrationResult(false, "This key has already been used.");
        }

        // Bind sensorId to unique key (mark it as used)
        keySensor.setSensorDataId(sensorID);
        sensorRepository.save(keySensor);

        // Create fresh sensor document for readings
        Sensor newSensorData = new Sensor(sensorID, uniqueKey, 0, 0, 0, 0);
        newSensorData.setTimestamp(Instant.now());
        mongoTemplate.save(newSensorData, "SensorData");

        return new RegistrationResult(true, null);
    }

    /**
     * Admin view: Sensor status with patient-hospital mapping
     */
    public List<PatientSensorStatus> getAllPatientSensorStatus() throws ExecutionException, InterruptedException {
        List<PatientSensorStatus> statusList = new ArrayList<>();
        List<Sensor> sensors = sensorRepository.findAll();

        for (Sensor sensor : sensors) {
            if (sensor.getSensorDataId() == null) {
                statusList.add(new PatientSensorStatus(
                        sensor.getUniqueKey(),
                        "",
                        "N/A",
                        null,
                        "-"
                ));
            } else {
                // Try find patient linked to this sensor ID
                Patient assignedPatient = patientService.getAllPatients().stream()
                        .filter(p -> sensor.getSensorDataId().equals(p.getSensorDataId()))
                        .findFirst()
                        .orElse(null);

                if (assignedPatient != null) {
    String hospital = "-";
    String doctorId = assignedPatient.getAssigned_doctor();

    if (doctorId != null && !doctorId.isEmpty()) {
        var doctor = doctorService.getDoctorById(doctorId);
        if (doctor != null && doctor.getHospital() != null) {
            hospital = doctor.getHospital();
        }
    }

    statusList.add(new PatientSensorStatus(
            sensor.getUniqueKey(),
            assignedPatient.getUserId(),
            assignedPatient.getName(),
            sensor.getSensorDataId(),
            hospital
    ));
}
else {
                    statusList.add(new PatientSensorStatus(
                            sensor.getUniqueKey(),
                            "?",
                            "Assigned to patient",
                            sensor.getSensorDataId(),
                            "?"
                    ));
                }
            }
        }

        return statusList;
    }
}
