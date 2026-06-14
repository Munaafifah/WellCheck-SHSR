package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.User.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @interface IUserManagementService
 *            Provided interface contract for user management.
 *            UserService realizes this interface.
 */
public interface IUserManagementService {

    String createUser(User user) throws ExecutionException, InterruptedException;

    String updateUser(User user) throws ExecutionException, InterruptedException;

    String deleteUser(String id) throws ExecutionException, InterruptedException;

    List<User> getAllUsers() throws ExecutionException, InterruptedException;

    User getUserById(String id) throws ExecutionException, InterruptedException;
}