package com.sparta.cr.carnasagameswebsiteandapi.services.interfaces;

import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserServiceable extends UserDetailsService {

    UserModel createUser(UserModel user);
    UserModel updateUser(UserModel user);
    UserModel deleteUser(Long userId);
    Optional<UserModel> getUser(Long userId);
    Optional<UserModel> getUserByEmail(String email);
    Optional<UserModel> getUserByUsername(String username);

    Page<UserModel> getAllUsers(int page, int size);
    Page<UserModel> getUsersByName(String name, int page, int size);

}
