

package com.SmartHealthRemoteSystem.SHSR.User;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;

@Repository
public class UserRepository implements SHSRDAO<User> {

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @Override
    public User get(String id) {
        System.out.println("Getting user by ID: " + id);
        return mongoUserRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAll() {
        System.out.println("Getting all users");
        return mongoUserRepository.findAll();
    }

    @Override
    public String save(User user) {
        System.out.println("Saving new user: " + user.getUserId());
        User savedUser = mongoUserRepository.save(user);
        return savedUser.getUserId();
    }

    @Override
public String update(User user) {
    System.out.println("Updating user: " + user.getUserId());

    Optional<User> optionalUser = mongoUserRepository.findById(user.getUserId());

    if (optionalUser.isPresent()) {
        User existingUser = optionalUser.get();

        if (user.getName() != null && !user.getName().isEmpty()) {
            existingUser.setName(user.getName());
        }

        if (user.getContact() != null && !user.getContact().isEmpty()) {
            existingUser.setContact(user.getContact());
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            existingUser.setEmail(user.getEmail());
        }

        // Optional: update password only if needed
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }

        mongoUserRepository.save(existingUser);
        return "User updated successfully.";
    }

    return "Error: User not found.";
}


    @Override
    public String delete(String id) {
        System.out.println("Deleting user: " + id);
        mongoUserRepository.deleteById(id);
        return id;
    }

    public boolean existsByEmail(String email) {
        return mongoUserRepository.existsByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return mongoUserRepository.findByEmail(email);
    }
}
