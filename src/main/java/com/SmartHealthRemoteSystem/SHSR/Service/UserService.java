package com.SmartHealthRemoteSystem.SHSR.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
import com.SmartHealthRemoteSystem.SHSR.User.User;

@Service
public class UserService implements IUserManagementService {

    private final SHSRDAO<User> userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public UserService(SHSRDAO<User> userRepository,
            PasswordEncoder passwordEncoder,
            PatientService patientService,
            DoctorService doctorService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @Override
    public String updateUser(User user) throws ExecutionException, InterruptedException {
        return userRepository.update(user);
    }

    @Override
    public String createUser(User user) throws ExecutionException, InterruptedException {
        List<User> userList = userRepository.getAll();

        boolean isTaken = userList.stream()
                .anyMatch(existingUser -> existingUser.getUserId().equals(user.getUserId())
                || (user.getEmail() != null && user.getEmail().equals(existingUser.getEmail())));

        if (isTaken) {
            return "Failed to create user. ID or Email already exists.";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(String userId) throws ExecutionException, InterruptedException {
        return userRepository.get(userId);
    }

    @Override
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        return userRepository.getAll();
    }

    @Override
    public String deleteUser(String userId) throws ExecutionException, InterruptedException {
        return userRepository.delete(userId);
    }

    // non-interface methods — these stay as-is, no @Override needed
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

    public List<User> getClinicAssistantList() throws ExecutionException, InterruptedException {
        return userRepository.getAll()
                .stream()
                .filter(user -> "CLINIC_ASSISTANT".equals(user.getRole()))
                .collect(Collectors.toList());
    }

    public User getUserByEmail(String email) throws ExecutionException, InterruptedException {
        if (email == null) {
            return null;
        }
        return userRepository.getAll().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst()
                .orElse(null);
    }
}
