package com.SmartHealthRemoteSystem.SHSR.User.Radiologist;

import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
import com.SmartHealthRemoteSystem.SHSR.User.MongoUserRepository;
import com.SmartHealthRemoteSystem.SHSR.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class RadiologistRepository implements SHSRDAO<Radiologist> {

    @Autowired
    private MongoRadiologistRepository mongoRadiologistRepository;

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Radiologist get(String id) throws ExecutionException, InterruptedException {
        Optional<Radiologist> opt = mongoRadiologistRepository.findById(id);
        if (opt.isPresent()) {
            Radiologist rl = opt.get();
            mongoUserRepository.findById(id).ifPresent(user -> {
                rl.setName(user.getName());
                rl.setPassword(user.getPassword());
                rl.setContact(user.getContact());
                rl.setRole(user.getRole());
                rl.setEmail(user.getEmail());
            });
            return rl;
        }
        return null;
    }

    @Override
    public List<Radiologist> getAll() throws ExecutionException, InterruptedException {
        List<Radiologist> list = new ArrayList<>();
        for (Radiologist rl : mongoRadiologistRepository.findAll()) {
            mongoUserRepository.findById(rl.getUserId()).ifPresent(user -> {
                rl.setName(user.getName());
                rl.setPassword(user.getPassword());
                rl.setContact(user.getContact());
                rl.setRole(user.getRole());
                rl.setEmail(user.getEmail());
            });
            list.add(rl);
        }
        return list;
    }

    @Override
    public String save(Radiologist rl) throws ExecutionException, InterruptedException {
        String encodedPassword = passwordEncoder.encode(rl.getPassword());
        User user = new User(rl.getUserId(), rl.getName(), encodedPassword,
                rl.getContact(), rl.getRole(), rl.getEmail());
        mongoUserRepository.save(user);
        rl.setPassword(encodedPassword);
        mongoRadiologistRepository.save(rl);
        return rl.getUserId();
    }

    @Override
    public String update(Radiologist rl) throws ExecutionException, InterruptedException {
        mongoUserRepository.findById(rl.getUserId()).ifPresent(user -> {
            if (rl.getName() != null && !rl.getName().isEmpty()) user.setName(rl.getName());
            if (rl.getContact() != null && !rl.getContact().isEmpty()) user.setContact(rl.getContact());
            if (rl.getEmail() != null && !rl.getEmail().isEmpty()) user.setEmail(rl.getEmail());
            mongoUserRepository.save(user);
        });

        mongoRadiologistRepository.findById(rl.getUserId()).ifPresent(existing -> {
            if (rl.getName() != null && !rl.getName().isEmpty()) existing.setName(rl.getName());
            if (rl.getContact() != null && !rl.getContact().isEmpty()) existing.setContact(rl.getContact());
            if (rl.getEmail() != null && !rl.getEmail().isEmpty()) existing.setEmail(rl.getEmail());
            if (rl.getDepartment() != null && !rl.getDepartment().isEmpty()) existing.setDepartment(rl.getDepartment());
            if (rl.getSpecialization() != null && !rl.getSpecialization().isEmpty()) existing.setSpecialization(rl.getSpecialization());
            if (rl.getProfilePicture() != null && !rl.getProfilePicture().isEmpty()) existing.setProfilePicture(rl.getProfilePicture());
            if (rl.getProfilePictureType() != null && !rl.getProfilePictureType().isEmpty()) existing.setProfilePictureType(rl.getProfilePictureType());
            mongoRadiologistRepository.save(existing);
        });

        return "Radiologist updated successfully.";
    }

    @Override
    public String delete(String id) throws ExecutionException, InterruptedException {
        mongoRadiologistRepository.deleteById(id);
        mongoUserRepository.deleteById(id);
        return id;
    }
}
