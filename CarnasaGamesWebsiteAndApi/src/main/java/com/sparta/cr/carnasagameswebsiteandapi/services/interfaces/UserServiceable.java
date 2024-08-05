package com.sparta.cr.carnasagameswebsiteandapi.services.interfaces;

import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;

import java.util.List;

public interface UserServiceable {

    UserModel createUser(UserModel user);
    UserModel updateUser(UserModel user);
    UserModel deleteUser(Long userId);
    UserModel getUser(Long userId);

    List<UserModel> getAllUsers();
    List<UserModel> getUsersByName(String name);

}
