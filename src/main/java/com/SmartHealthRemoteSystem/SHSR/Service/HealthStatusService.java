

//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.SendDailyHealth.HealthStatus;
import com.SmartHealthRemoteSystem.SHSR.SendDailyHealth.HealthStatusDocumentWrapper;
import com.SmartHealthRemoteSystem.SHSR.SendDailyHealth.HealthStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class HealthStatusService {

    @Autowired
    private HealthStatusRepository healthStatusRepository;

    public String createHealthStatus(HealthStatus healthStatus, String patientId) {
        healthStatus.setHealthStatusId(UUID.randomUUID().toString());
        healthStatus.setTimestamp(Instant.now());



        HealthStatusDocumentWrapper wrapper = new HealthStatusDocumentWrapper();
        wrapper.setPatientId(patientId);
        wrapper.setHealthStatus(healthStatus);

        return healthStatusRepository.save(wrapper).getId(); // return wrapper ID
    }

    public List<HealthStatus> getListHealthStatus(String patientId) {
        List<HealthStatusDocumentWrapper> wrappers = healthStatusRepository.findByPatientId(patientId);
        return wrappers.stream()
                       .map(HealthStatusDocumentWrapper::getHealthStatus)
                       .collect(Collectors.toList());
    }

    public HealthStatus getHealthStatus(String healthStatusId, String patientId) {
        return healthStatusRepository.findByPatientId(patientId).stream()
                .map(HealthStatusDocumentWrapper::getHealthStatus)
                .filter(hs -> hs.getHealthStatusId().equals(healthStatusId))
                .findFirst()
                .orElse(null);
    }

    public void updateHealthStatus(HealthStatus updated, String patientId) {
        List<HealthStatusDocumentWrapper> wrappers = healthStatusRepository.findByPatientId(patientId);

        for (HealthStatusDocumentWrapper wrapper : wrappers) {
            HealthStatus hs = wrapper.getHealthStatus();
            if (hs.getHealthStatusId().equals(updated.getHealthStatusId())) {
                hs.setDoctorId(updated.getDoctorId());
                hs.setAdditionalNotes(updated.getAdditionalNotes());
                hs.setTimestamp(Instant.now());
                healthStatusRepository.save(wrapper);
                break;
            }
        }
    }

    public String deleteHealthStatus(String healthStatusId, String patientId) {
        List<HealthStatusDocumentWrapper> wrappers = healthStatusRepository.findByPatientId(patientId);

        for (HealthStatusDocumentWrapper wrapper : wrappers) {
            if (wrapper.getHealthStatus().getHealthStatusId().equals(healthStatusId)) {
                healthStatusRepository.delete(wrapper);
                return "Deleted HealthStatus with ID: " + healthStatusId;
            }
        }
        return "HealthStatus not found";
    }
}
