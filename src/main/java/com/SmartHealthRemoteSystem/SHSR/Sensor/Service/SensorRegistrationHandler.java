package com.SmartHealthRemoteSystem.SHSR.Sensor.Service;

import com.SmartHealthRemoteSystem.SHSR.Sensor.Model.Sensor;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Model.PatientSensorStatus;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Model.SensorRequest;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Repository.SensorRepository;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Repository.SensorRequestRepository;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.MailService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Sensor.RegistrationResult;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class SensorRegistrationHandler {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private SensorRequestRepository sensorRequestRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UniqueKeyGenerator uniqueKeyGenerator;

    // ─────────────────────────────────────────────────────────────
    // Register sensor using uniqueKey (existing — keep as-is)
    // ─────────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────────
    // NEW: Approve sensor request → generate key → email patient
    // ─────────────────────────────────────────────────────────────
    public void approveSensorRequest(String requestId) {
        // 1. Find the request
        Optional<SensorRequest> optionalRequest = sensorRequestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            throw new RuntimeException("Sensor request not found: " + requestId);
        }

        SensorRequest sensorRequest = optionalRequest.get();

        // 2. Check it's still PENDING
        if (!sensorRequest.getStatus().equals("PENDING")) {
            throw new RuntimeException("Request is no longer pending.");
        }

        // 3. Generate unique key and store into Sensor collection
        String uniqueKey = uniqueKeyGenerator.generateAndStore();

        // 4. Mark request as APPROVED
        sensorRequest.setStatus("APPROVED");
        sensorRequestRepository.save(sensorRequest);

        // 5. Send unique key to patient email
        String subject = "WellCheck — Your Sensor Unique Key";
        String message = "Dear " + sensorRequest.getPatientName() + ",\n\n"
                + "Your sensor registration request has been approved by the administrator.\n\n"
                + "Your Unique Key is:\n\n"
                + "    " + uniqueKey + "\n\n"
                + "Please log in to WellCheck and go to Register Sensor to complete your registration.\n"
                + "Enter your Sensor ID and the Unique Key above to activate your device.\n\n"
                + "Regards,\n"
                + "WellCheck System";

        mailService.sendMail(sensorRequest.getPatientEmail(), subject, message);
    }

    // ─────────────────────────────────────────────────────────────
    // Admin view: Sensor status with patient-hospital mapping
    // (existing — keep as-is)
    // ─────────────────────────────────────────────────────────────
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
                } else {
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