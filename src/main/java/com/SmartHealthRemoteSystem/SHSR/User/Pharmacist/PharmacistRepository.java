
//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.User.Pharmacist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
import com.SmartHealthRemoteSystem.SHSR.User.MongoUserRepository;
import com.SmartHealthRemoteSystem.SHSR.User.User;

@Repository
public class PharmacistRepository implements SHSRDAO<Pharmacist> {

    @Autowired
    private MongoPharmacistRepository mongoPharmacistRepository;

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public Pharmacist get(String pharmacistId) throws ExecutionException, InterruptedException {
        Optional<Pharmacist> pharmacistOpt = mongoPharmacistRepository.findById(pharmacistId);
        if (pharmacistOpt.isPresent()) {
            Pharmacist pharmacist = pharmacistOpt.get();
            mongoUserRepository.findById(pharmacistId).ifPresent(user -> {
                pharmacist.setName(user.getName());
                pharmacist.setPassword(user.getPassword());
                pharmacist.setContact(user.getContact());
                pharmacist.setRole(user.getRole());
                pharmacist.setEmail(user.getEmail());
            });
            return pharmacist;
        }
        return null;
    }

    @Override
    public List<Pharmacist> getAll() throws ExecutionException, InterruptedException {
        List<Pharmacist> list = new ArrayList<>();
        for (Pharmacist pharmacist : mongoPharmacistRepository.findAll()) {
            mongoUserRepository.findById(pharmacist.getUserId()).ifPresent(user -> {
                pharmacist.setName(user.getName());
                pharmacist.setPassword(user.getPassword());
                pharmacist.setContact(user.getContact());
                pharmacist.setRole(user.getRole());
                pharmacist.setEmail(user.getEmail());
            });
            list.add(pharmacist);
        }
        return list;
    }

    // @Override
    // public String save(Pharmacist pharmacist) throws ExecutionException, InterruptedException {
    //     User user = new User(pharmacist.getUserId(), pharmacist.getName(), pharmacist.getPassword(),
    //             pharmacist.getContact(), pharmacist.getRole(), pharmacist.getEmail());
    //     mongoUserRepository.save(user);
    //     mongoPharmacistRepository.save(pharmacist);
    //     return pharmacist.getUserId();
    // }

    @Override
public String save(Pharmacist pharmacist) throws ExecutionException, InterruptedException {
    System.out.println("📝 Saving pharmacist with ID: " + pharmacist.getUserId());

    // Encode the password before saving
    String encodedPassword = passwordEncoder.encode(pharmacist.getPassword());
    pharmacist.setPassword(encodedPassword);  // Update password inside pharmacist object

    // Save into Pharmacist collection
    mongoPharmacistRepository.save(pharmacist);

    // Also save into User collection
    User user = new User(
        pharmacist.getUserId(),
        pharmacist.getName(),
        encodedPassword,
        pharmacist.getContact(),
        pharmacist.getRole(),
        pharmacist.getEmail()
    );
    mongoUserRepository.save(user);

    return pharmacist.getUserId();
}


    @Override
    public String update(Pharmacist pharmacist) throws ExecutionException, InterruptedException {
        mongoUserRepository.save(new User(
            pharmacist.getUserId(),
            pharmacist.getName(),
            pharmacist.getPassword(),
            pharmacist.getContact(),
            pharmacist.getRole(),
            pharmacist.getEmail()
        ));
        mongoPharmacistRepository.save(pharmacist);
        return pharmacist.getUserId();
    }

    @Override
    public String delete(String id) throws ExecutionException, InterruptedException {
        mongoUserRepository.deleteById(id);
        mongoPharmacistRepository.deleteById(id);
        return id;
    }
}

