package com.SmartHealthRemoteSystem.SHSR.Utility;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.SmartHealthRemoteSystem.SHSR.User.MongoUserRepository;
import com.SmartHealthRemoteSystem.SHSR.User.User;

@Component
public class MigrationUtility {

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct  // Automatically runs once after app starts
    public void encodeMigratedPasswords() {
        List<User> users = mongoUserRepository.findAll();

        for (User user : users) {
            String rawPassword = user.getPassword();

            if (rawPassword != null && !rawPassword.startsWith("$2a$")) {
                String encoded = passwordEncoder.encode(rawPassword);
                user.setPassword(encoded);
                mongoUserRepository.save(user);
                System.out.println("Encoded password for: " + user.getUserId());
            } else if (rawPassword == null) {
                System.out.println("Skipped user with null password: " + user.getUserId());
            } else {
                System.out.println("Already encoded: " + user.getUserId());
            }
        }

        System.out.println("Migration password encoding complete.");
    }
}
