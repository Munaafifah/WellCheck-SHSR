package com.SmartHealthRemoteSystem.SHSR.User.ClinicAssistant;

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
public class ClinicAssistantRepository implements SHSRDAO<ClinicAssistant> {

    @Autowired
    private MongoClinicAssistantRepository mongoClinicAssistantRepository;

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ClinicAssistant get(String id) throws ExecutionException, InterruptedException {
        Optional<ClinicAssistant> opt = mongoClinicAssistantRepository.findById(id);
        if (opt.isPresent()) {
            ClinicAssistant ca = opt.get();
            mongoUserRepository.findById(id).ifPresent(user -> {
                ca.setName(user.getName());
                ca.setPassword(user.getPassword());
                ca.setContact(user.getContact());
                ca.setRole(user.getRole());
                ca.setEmail(user.getEmail());
            });
            return ca;
        }
        return null;
    }

    @Override
    public List<ClinicAssistant> getAll() throws ExecutionException, InterruptedException {
        List<ClinicAssistant> list = new ArrayList<>();
        for (ClinicAssistant ca : mongoClinicAssistantRepository.findAll()) {
            mongoUserRepository.findById(ca.getUserId()).ifPresent(user -> {
                ca.setName(user.getName());
                ca.setPassword(user.getPassword());
                ca.setContact(user.getContact());
                ca.setRole(user.getRole());
                ca.setEmail(user.getEmail());
            });
            list.add(ca);
        }
        return list;
    }

    @Override
    public String save(ClinicAssistant ca) throws ExecutionException, InterruptedException {
        // Save to User collection
        String encodedPassword = passwordEncoder.encode(ca.getPassword());
        User user = new User(ca.getUserId(), ca.getName(), encodedPassword,
                ca.getContact(), ca.getRole(), ca.getEmail());
        mongoUserRepository.save(user);

        // Save to ClinicAssistant collection (with hashed password)
        ca.setPassword(encodedPassword);
        mongoClinicAssistantRepository.save(ca);
        return ca.getUserId();
    }

    @Override
    public String update(ClinicAssistant ca) throws ExecutionException, InterruptedException {
        // Update User collection
        mongoUserRepository.findById(ca.getUserId()).ifPresent(user -> {
            if (ca.getName() != null && !ca.getName().isEmpty())
                user.setName(ca.getName());
            if (ca.getContact() != null && !ca.getContact().isEmpty())
                user.setContact(ca.getContact());
            if (ca.getEmail() != null && !ca.getEmail().isEmpty())
                user.setEmail(ca.getEmail());
            mongoUserRepository.save(user);
        });

        // Update ClinicAssistant collection
        mongoClinicAssistantRepository.findById(ca.getUserId()).ifPresent(existing -> {
            if (ca.getName() != null && !ca.getName().isEmpty())
                existing.setName(ca.getName());
            if (ca.getContact() != null && !ca.getContact().isEmpty())
                existing.setContact(ca.getContact());
            if (ca.getEmail() != null && !ca.getEmail().isEmpty())
                existing.setEmail(ca.getEmail());
            if (ca.getClinic() != null && !ca.getClinic().isEmpty())
                existing.setClinic(ca.getClinic());
            if (ca.getPosition() != null && !ca.getPosition().isEmpty())
                existing.setPosition(ca.getPosition());
            // ✅ ADD THESE TWO
            if (ca.getProfilePicture() != null && !ca.getProfilePicture().isEmpty())
                existing.setProfilePicture(ca.getProfilePicture());
            if (ca.getProfilePictureType() != null && !ca.getProfilePictureType().isEmpty())
                existing.setProfilePictureType(ca.getProfilePictureType());
            mongoClinicAssistantRepository.save(existing);
        });

        return "Clinic Assistant updated successfully.";
    }

    @Override
    public String delete(String id) throws ExecutionException, InterruptedException {
        mongoClinicAssistantRepository.deleteById(id);
        mongoUserRepository.deleteById(id);
        return id;
    }
}