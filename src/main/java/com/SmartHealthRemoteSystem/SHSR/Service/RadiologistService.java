package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.User.Radiologist.Radiologist;
import com.SmartHealthRemoteSystem.SHSR.User.Radiologist.RadiologistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class RadiologistService {

    @Autowired
    private RadiologistRepository radiologistRepository;

    public String createRadiologist(Radiologist rl) throws ExecutionException, InterruptedException {
        return radiologistRepository.save(rl);
    }

    public Radiologist getRadiologist(String id) throws ExecutionException, InterruptedException {
        return radiologistRepository.get(id);
    }

    public List<Radiologist> getListRadiologist() throws ExecutionException, InterruptedException {
        return radiologistRepository.getAll();
    }

    public String updateRadiologist(Radiologist rl) throws ExecutionException, InterruptedException {
        return radiologistRepository.update(rl);
    }

    public String deleteRadiologist(String id) throws ExecutionException, InterruptedException {
        return radiologistRepository.delete(id);
    }
}
