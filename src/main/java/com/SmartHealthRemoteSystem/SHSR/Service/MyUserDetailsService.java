
package com.SmartHealthRemoteSystem.SHSR.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.SmartHealthRemoteSystem.SHSR.User.MongoUserRepository;
import com.SmartHealthRemoteSystem.SHSR.User.User;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final MongoUserRepository mongoUserRepository;

    @Autowired
    public MyUserDetailsService(MongoUserRepository mongoUserRepository) {
        this.mongoUserRepository = mongoUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        System.out.println(" Attempting login with userId: " + userId);

        // Find by ID from MongoDB
        User user = mongoUserRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));

        return new MyUserDetails(user);
    }
}
