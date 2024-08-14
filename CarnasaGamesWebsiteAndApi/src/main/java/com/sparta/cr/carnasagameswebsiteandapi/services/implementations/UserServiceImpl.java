package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;
import com.sparta.cr.carnasagameswebsiteandapi.config.CensorConfig;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidUserException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelAlreadyExistsException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.*;
import com.sparta.cr.carnasagameswebsiteandapi.models.*;
import com.sparta.cr.carnasagameswebsiteandapi.models.dtos.UpdatePasswordDto;
import com.sparta.cr.carnasagameswebsiteandapi.models.dtos.UserDto;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.FollowerRepository;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.UserRepository;
import com.sparta.cr.carnasagameswebsiteandapi.security.SecurityUser;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.UserServiceable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl extends DefaultOAuth2UserService implements UserServiceable {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CensorConfig censorConfig;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, CensorConfig censorConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.censorConfig = censorConfig;
    }

//    @Override
//    @Transactional
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        String email = (String) attributes.get("email");
//        String username = (String) attributes.getOrDefault("username", "");
//        String profileUrl = (String) attributes.getOrDefault("profileUrl", "");
//
//        if(email==null || email.isEmpty()){
//            throw new InvalidUserException("Email not found in attributes");
//        }
//        if(username.isEmpty()){
//            username = generateUsernameFromEmail(email);
//        }
//
//        Optional<UserModel> optionalUser = getUserByEmail(email);
//        UserModel userModel;
//
//        if(optionalUser.isEmpty()){
//            username = generateUniqueUsername(username);
//            UserModel user = new UserModel();
//            user.setEmail(email);
//            user.setUsername(username);
//            user.setRoles(Set.of("ROLE_USER"));
//            user.setProfileImage(profileUrl);
//            user.setPassword("PlaceholderPasswordToNotBreakTheModel");
//            user.setPrivate(false);
//            userModel = userRepository.save(user);
//        }
//        else {
//            userModel = optionalUser.get();
//        }
//        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(userModel.getRoles())), attributes, "email");
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).map(SecurityUser::new).orElseThrow(() -> new UsernameNotFoundException("User not found: " +username));
    }

    @Override
    public Page<UserModel> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }


    @Override
    public Page<UserModel> getUsersByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByUsernameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Optional<UserModel> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserDto convertUserToDto(UserModel user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImage(),
                user.getDescription(),
                user.getRoles(),
                user.isPrivate());
    }

    @Override
    public Optional<UserModel> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserModel> getUser(Long userId){
        return userRepository.findById(userId);
    }

    @Override
    public UserModel createUser(UserModel user) {
        if(validateNewUser(user)) {
            user.setRoles(Set.of("ROLE_USER"));
            user.setEmail(user.getEmail().toLowerCase());
            return userRepository.save(encryptPassword(user));
        }
        return null;
    }

    @Override
    public UserModel updateUser(UserModel user) {
        if(validateExistingUserUpdate(user)){
            user.setEmail(user.getEmail().toLowerCase());
            return userRepository.save(encryptPassword(user));
        }
        else {
            return null;
        }
    }

    public UserModel updateUserRoles(Long userId) {
        //only want to add or remove ROLE_ADMIN
        if(getUser(userId).isEmpty()){
            throw new UserNotFoundException(userId.toString());
        }
        else {
            UserModel update = getUser(userId).get();
            if(update.getRoles().contains("ROLE_ADMIN")){
                update.getRoles().remove("ROLE_ADMIN");
            }
            else{
                update.getRoles().add("ROLE_ADMIN");
            }
            return userRepository.save(update);
        }
    }

    public UserModel updateUserPassword(UpdatePasswordDto updatePasswordDto) {
        if(getUser(updatePasswordDto.getId()).isEmpty()){
            throw new UserNotFoundException(updatePasswordDto.getId().toString());
        }
        else{
            UserModel update = getUser(updatePasswordDto.getId()).get();
            if(!passwordEncoder.matches(updatePasswordDto.getOldPassword(), update.getPassword())){
                throw new InvalidUserException("Old password does not match");
            }
            if(!validatePassword(updatePasswordDto.getNewPassword())){
                throw new InvalidPasswordException();
            }
            if(updatePasswordDto.getNewPassword().equals(updatePasswordDto.getConfirmPassword())){
                update.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
                return userRepository.save(update);
            }
            else {
                throw new InvalidUserException("New password and confirmation password do not match. Please ensure both fields contain the same password");
            }
        }
    }

    @Override
    public UserModel deleteUser(Long userId) {
        Optional<UserModel> user = getUser(userId);
        user.ifPresent(userModel -> userRepository.delete(userModel));
        return user.orElse(null);
    }

    public boolean validateNewUser(UserModel user){
        if(getUser(user.getId()).isPresent()){
            throw new ModelAlreadyExistsException("Cannot create new User with ID: " + user.getId() + " already exists");
        }
        else if(!validateUsername(user.getUsername())){
            throw new InvalidUsernameException(user.getUsername());
        }
        else if (censorConfig.censorBadText(user.getUsername()).contains("*")) {
            throw new InvalidUserException("Username cannot contain inappropriate language.");
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
        else if(censorConfig.censorBadText(user.getDescription()).contains("*")) {
            throw new InvalidUserException("User description cannot contain inappropriate language.");
        }
        return true;
    }

    public boolean validateExistingUserUpdate(UserModel user){
        if(getUser(user.getId()).isEmpty()){
            throw new ModelNotFoundException("Cannot update User as ID: " + user.getId() + " does not exist");
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
        if(censorConfig.censorBadText(user.getDescription()).contains("*")) {
            throw new InvalidUserException("User description cannot contain inappropriate language.");
        }
        return true;
    }

    public boolean validatePassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }
    private boolean usernameExists(UserModel user) {
        return getUserByUsername(user.getUsername()).isEmpty();
    }
    private boolean validateUsername(String username) {
        return username.matches("[a-zA-Z0-9_-]{3,20}$");
    }
    private boolean validateEmail(String email) {
        email = email.toLowerCase();
        return email.matches("^([a-zA-Z0-9_\\-.]+)@([a-z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$");
    }
    private boolean emailExists(UserModel user) {
        return getUserByEmail(user.getEmail().toLowerCase()).isEmpty();
    }
    private UserModel encryptPassword(UserModel user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }
    private String generateUsernameFromEmail(String email) {
        String baseUsername = email.split("@")[0];
        baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9_-]", "");
        if (!baseUsername.matches("[a-zA-Z0-9_-]{3,20}$")) {
            baseUsername = "user" + System.currentTimeMillis(); // Fallback username
        }
        return baseUsername;
    }

    private String generateUniqueUsername(String baseUsername) {
        String updatedUsername = baseUsername;
        int counter = 0;
        while (getUserByUsername(updatedUsername).isPresent()) {
            updatedUsername = baseUsername + "_" + counter;
            counter++;
            if(updatedUsername.length()>30){
                throw new InvalidUsernameException(updatedUsername);
            }
        }
        return updatedUsername;
    }
}
