
package com.SmartHealthRemoteSystem.SHSR.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
import com.SmartHealthRemoteSystem.SHSR.User.User;
// import com.SmartHealthRemoteSystem.SHSR.User.UserWithDetails;

@Service
public class UserService {
    private final SHSRDAO<User> userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientService patientService;
    private final DoctorService doctorService;
    // private final PharmacistService pharmacistService;


    public UserService(SHSRDAO<User> userRepository,
                   PasswordEncoder passwordEncoder,
                   PatientService patientService,
                   DoctorService doctorService               /* ← stop here */
) {
    this.userRepository   = userRepository;
    this.passwordEncoder  = passwordEncoder;
    this.patientService   = patientService;
    this.doctorService    = doctorService;
}



    public String updateUser(User user) throws ExecutionException, InterruptedException {
        return userRepository.update(user);
    }

    public String createUser(User user) throws ExecutionException, InterruptedException {
        List<User> userList = userRepository.getAll();
        
        boolean isTaken = userList.stream()
                .anyMatch(existingUser -> 
                    existingUser.getUserId().equals(user.getUserId()) || 
                    (user.getEmail() != null && user.getEmail().equals(existingUser.getEmail()))
                );
        
        if (isTaken) {
            return "Failed to create user. ID or Email already exists.";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        return userRepository.save(user);
    }

    public User getUser(String userId) throws ExecutionException, InterruptedException {
        return userRepository.get(userId);
    }

    public List<User> getUserList() throws ExecutionException, InterruptedException {
        return userRepository.getAll();
    }

    public String deleteUser(String userId) throws ExecutionException, InterruptedException {
        return userRepository.delete(userId);
    }

    public List<User> getAdminList() throws ExecutionException, InterruptedException {
        return userRepository.getAll()
                .stream()
                .filter(user -> "ADMIN".equals(user.getRole()))
                .collect(Collectors.toList());
    }
 
    public List<User> searchUsers(String keyword) throws ExecutionException, InterruptedException {
        return userRepository.getAll()
                .stream()
                .filter(user -> user.getUserId().contains(keyword) || user.getName().contains(keyword))
                .collect(Collectors.toList());
    }
    // public List<UserWithDetails> getAllUsersWithDetails() throws ExecutionException, InterruptedException {
    //     List<User> users = userRepository.getAll();
    //     List<UserWithDetails> userWithDetailsList = new ArrayList<>();
    
    //     for (User user : users) {
    //         Object details = null;
    
    //         switch (user.getRole()) {
    //             case "PATIENT":
    //                 details = patientService.getPatientById(user.getUserId());
    //                 if (details == null) details = new Object();
    //                 break;
    //             case "DOCTOR":
    //                 details = doctorService.getDoctor(user.getUserId());
    //                 if (details == null) details = new Object();
    //                 break;
    //             case "PHARMACIST":
    //                 details = pharmacistService.getPharmacist(user.getUserId());
    //                 if (details == null) details = new Object();
    //                 break;
    //             default:
    //                 details = null;
    //         }
    
    //         userWithDetailsList.add(new UserWithDetails(user, details));
    //     }
    
    //     return userWithDetailsList;
    // }
    
    
    

}
