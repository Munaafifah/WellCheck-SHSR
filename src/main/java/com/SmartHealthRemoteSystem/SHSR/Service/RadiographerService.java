package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.User.Radiographer.Radiographer;
import com.SmartHealthRemoteSystem.SHSR.User.Radiographer.RadiographerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class RadiographerService {

    @Autowired
    private RadiographerRepository radiographerRepository;

    public String createRadiographer(Radiographer rg) throws ExecutionException, InterruptedException {
        return radiographerRepository.save(rg);
    }

    public Radiographer getRadiographer(String id) throws ExecutionException, InterruptedException {
        return radiographerRepository.get(id);
    }

    public List<Radiographer> getListRadiographer() throws ExecutionException, InterruptedException {
        return radiographerRepository.getAll();
    }

    public String updateRadiographer(Radiographer rg) throws ExecutionException, InterruptedException {
        return radiographerRepository.update(rg);
    }

    public String deleteRadiographer(String id) throws ExecutionException, InterruptedException {
        return radiographerRepository.delete(id);
    }
}
