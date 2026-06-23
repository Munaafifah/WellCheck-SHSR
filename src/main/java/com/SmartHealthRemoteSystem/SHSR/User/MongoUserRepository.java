package com.SmartHealthRemoteSystem.SHSR.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoUserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Used to broadcast notifications to every active staff member of a given
    // role (e.g. all RADIOLOGIST / RADIOGRAPHER users) — see UCR012.
    List<User> findByRole(String role);
}