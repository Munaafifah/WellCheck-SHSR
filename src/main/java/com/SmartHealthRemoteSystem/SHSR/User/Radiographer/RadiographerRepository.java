package com.SmartHealthRemoteSystem.SHSR.User.Radiographer;

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
public class RadiographerRepository implements SHSRDAO<Radiographer> {

    @Autowired
    private MongoRadiographerRepository mongoRadiographerRepository;

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Radiographer get(String id) throws ExecutionException, InterruptedException {
        Optional<Radiographer> opt = mongoRadiographerRepository.findById(id);
        if (opt.isPresent()) {
            Radiographer rg = opt.get();
            mongoUserRepository.findById(id).ifPresent(user -> {
                rg.setName(user.getName());
                rg.setPassword(user.getPassword());
                rg.setContact(user.getContact());
                rg.setRole(user.getRole());
                rg.setEmail(user.getEmail());
            });
            return rg;
        }
        return null;
    }

    @Override
    public List<Radiographer> getAll() throws ExecutionException, InterruptedException {
        List<Radiographer> list = new ArrayList<>();
        for (Radiographer rg : mongoRadiographerRepository.findAll()) {
            mongoUserRepository.findById(rg.getUserId()).ifPresent(user -> {
                rg.setName(user.getName());
                rg.setPassword(user.getPassword());
                rg.setContact(user.getContact());
                rg.setRole(user.getRole());
                rg.setEmail(user.getEmail());
            });
            list.add(rg);
        }
        return list;
    }

    @Override
    public String save(Radiographer rg) throws ExecutionException, InterruptedException {
        String encodedPassword = passwordEncoder.encode(rg.getPassword());
        User user = new User(rg.getUserId(), rg.getName(), encodedPassword,
                rg.getContact(), rg.getRole(), rg.getEmail());
        mongoUserRepository.save(user);
        rg.setPassword(encodedPassword);
        mongoRadiographerRepository.save(rg);
        return rg.getUserId();
    }

    @Override
    public String update(Radiographer rg) throws ExecutionException, InterruptedException {
        mongoUserRepository.findById(rg.getUserId()).ifPresent(user -> {
            if (rg.getName() != null && !rg.getName().isEmpty()) user.setName(rg.getName());
            if (rg.getContact() != null && !rg.getContact().isEmpty()) user.setContact(rg.getContact());
            if (rg.getEmail() != null && !rg.getEmail().isEmpty()) user.setEmail(rg.getEmail());
            mongoUserRepository.save(user);
        });

        mongoRadiographerRepository.findById(rg.getUserId()).ifPresent(existing -> {
            if (rg.getName() != null && !rg.getName().isEmpty()) existing.setName(rg.getName());
            if (rg.getContact() != null && !rg.getContact().isEmpty()) existing.setContact(rg.getContact());
            if (rg.getEmail() != null && !rg.getEmail().isEmpty()) existing.setEmail(rg.getEmail());
            if (rg.getDepartment() != null && !rg.getDepartment().isEmpty()) existing.setDepartment(rg.getDepartment());
            if (rg.getPosition() != null && !rg.getPosition().isEmpty()) existing.setPosition(rg.getPosition());
            if (rg.getProfilePicture() != null && !rg.getProfilePicture().isEmpty()) existing.setProfilePicture(rg.getProfilePicture());
            if (rg.getProfilePictureType() != null && !rg.getProfilePictureType().isEmpty()) existing.setProfilePictureType(rg.getProfilePictureType());
            mongoRadiographerRepository.save(existing);
        });

        return "Radiographer updated successfully.";
    }

    @Override
    public String delete(String id) throws ExecutionException, InterruptedException {
        mongoRadiographerRepository.deleteById(id);
        mongoUserRepository.deleteById(id);
        return id;
    }
}
