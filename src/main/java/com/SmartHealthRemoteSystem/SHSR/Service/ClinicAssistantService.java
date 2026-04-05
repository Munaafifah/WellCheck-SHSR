package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.User.ClinicAssistant.ClinicAssistant;
import com.SmartHealthRemoteSystem.SHSR.User.ClinicAssistant.ClinicAssistantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ClinicAssistantService {

    @Autowired
    private ClinicAssistantRepository clinicAssistantRepository;

    public String createClinicAssistant(ClinicAssistant ca) throws ExecutionException, InterruptedException {
        return clinicAssistantRepository.save(ca);
    }

    public ClinicAssistant getClinicAssistant(String id) throws ExecutionException, InterruptedException {
        return clinicAssistantRepository.get(id);
    }

    public List<ClinicAssistant> getListClinicAssistant() throws ExecutionException, InterruptedException {
        return clinicAssistantRepository.getAll();
    }

    public String updateClinicAssistant(ClinicAssistant ca) throws ExecutionException, InterruptedException {
        return clinicAssistantRepository.update(ca);
    }

    public String deleteClinicAssistant(String id) throws ExecutionException, InterruptedException {
        return clinicAssistantRepository.delete(id);
    }
}