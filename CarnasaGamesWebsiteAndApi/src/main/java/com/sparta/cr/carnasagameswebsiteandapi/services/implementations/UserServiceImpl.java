package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.UserRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.UserServiceable;
import jakarta.persistence.Id;
import org.springdoc.core.converters.AdditionalModelsConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserServiceable {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<UserModel> getUsersByName(String name) {
        return getAllUsers().stream().filter(userModel -> userModel.getUsername().contains(name)).toList();
    }

    @Override
    public Optional<UserModel> getUserByUsername(String username) {
        List<UserModel> users = getAllUsers();
        for (UserModel user : users) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserModel> getUserByEmail(String email) {
        List<UserModel> users = getAllUsers();
        for (UserModel user : users) {
            if (user.getEmail().equals(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserModel> getUser(Long userId){
        return userRepository.findById(userId);
    }

    @Override
    public UserModel createUser(UserModel user) {
        if(!validateNewUser(user)){
            return null;
        }
        return userRepository.save(encryptPassword(user));
    }

    @Override
    public UserModel updateUser(UserModel user) {
        if(getUser(user.getId()).isEmpty()){
            return null;
        }
        if(user.getPassword().equals(getUser(user.getId()).get().getPassword())){
            if(!validateEmail(user.getEmail())){
                return null;
            }
            return userRepository.save(user);
        }
        else {
            if(!validateUserDetails(user)){
                return null;
            }
            return userRepository.save(encryptPassword(user));
        }
        //todo validation for images and other fields
    }

    @Override
    public UserModel deleteUser(Long userId) {
        Optional<UserModel> user = getUser(userId);
        user.ifPresent(userModel -> userRepository.delete(userModel));
        return user.orElse(null);
    }

    public boolean validateNewUser(UserModel user){
        if(getUser(user.getId()).isPresent()){
            //user already exists exception
            return false;
        }
        else if(!validateUsername(user.getUsername())){
            //invalid username exception
            return false;
        }
        else if(!usernameExists(user)){
            //username exists exception
            return false;
        }
        else if(!validateEmail(user.getEmail())){
            //validate email exception
            return false;
        } else if (!emailExists(user)) {
            //email exists exception
            return false;
        } else if(!validatePassword(user.getPassword())){
            // validate password exception
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }
    private boolean usernameExists(UserModel user) {
        return getUserByUsername(user.getUsername()).isEmpty();
    }
    private boolean validateUsername(String username) {
        return username.matches("[a-zA-Z0-9_-]{3,20}$");
    }
    private boolean validateEmail(String email) {
        return email.matches("^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$");
    }
    private boolean emailExists(UserModel user) {
        return getUserByEmail(user.getEmail()).isEmpty();
    }
    private boolean validateUserDetails(UserModel user) {
        return validateEmail(user.getEmail()) && validatePassword(user.getPassword());
    }
    private UserModel encryptPassword(UserModel user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }
}
