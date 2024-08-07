package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.*;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.UserRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.UserServiceable;
import org.springframework.beans.factory.annotation.Autowired;
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
        user.setRoles("ROLE_USER");
        return userRepository.save(encryptPassword(user));
    }

    @Override
    public UserModel updateUser(UserModel user) {
        if(getUser(user.getId()).isEmpty()){
            return null;
        }
        UserModel beforeUpdate = getUser(user.getId()).get();
        if(!validateExistingUserUpdate(user)){
            return null;
        }
        else {
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
            throw new UserAlreadyExistsException(user.getId().toString());
        }
        else if(!validateUsername(user.getUsername())){
            throw new InvalidUsernameException(user.getUsername());
        }
        else if(!usernameExists(user)){
            throw new UsernameAlreadyExistsException(user.getUsername());
        }
        else if(!validateEmail(user.getEmail())){
            throw new InvalidEmailException(user.getEmail());
        }
        else if (!emailExists(user)) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        else if(!validatePassword(user.getPassword())){
            throw new InvalidPasswordException();
        }
        return true;
    }

    public boolean validateExistingUserUpdate(UserModel user){
        if(getUser(user.getId()).isEmpty()){
            return false;
        }
        UserModel beforeUpdate = getUser(user.getId()).get();
        if(!passwordEncoder.matches(user.getPassword(), beforeUpdate.getPassword())){
            if(!validatePassword(user.getPassword())){
                throw new InvalidPasswordException();
            }
        }
        if(!beforeUpdate.getEmail().equals(user.getEmail())){
            if(!validateEmail(user.getEmail())){
                throw new InvalidEmailException(user.getEmail());
            }
            else if(!emailExists(user)){
                throw new EmailAlreadyExistsException(user.getEmail());
            }
        }
        if(!beforeUpdate.getUsername().equals(user.getUsername())){
            throw new CantChangeUsernameException();
        }
        if(!beforeUpdate.getRoles().equals(user.getRoles())){
            if(!user.getRoles().equals("ROLE_USER") || !user.getRoles().equals("ROLE_ADMIN")){
                throw new InvalidRoleException();
            }
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
