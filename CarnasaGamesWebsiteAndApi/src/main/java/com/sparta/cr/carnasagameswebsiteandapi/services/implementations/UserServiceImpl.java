package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidUserException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelAlreadyExistsException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.*;
import com.sparta.cr.carnasagameswebsiteandapi.models.*;
import com.sparta.cr.carnasagameswebsiteandapi.models.dtos.UserDto;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.FollowerRepository;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.UserRepository;
import com.sparta.cr.carnasagameswebsiteandapi.security.SecurityUser;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.UserServiceable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    private UserRepository userRepository;
    private FollowerRepository followerRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, FollowerRepository followerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String username = (String) attributes.getOrDefault("username", "");

        if(email==null || email.isEmpty()){
            throw new InvalidUserException("Email not found in attributes");
        }
        if(username.isEmpty()){
            username = generateUsernameFromEmail(email);
        }

        Optional<UserModel> optionalUser = getUserByEmail(email);
        UserModel userModel;

        if(optionalUser.isEmpty()){
            username = generateUniqueUsername(username);
            UserModel user = new UserModel();
            user.setEmail(email);
            user.setUsername(username);
            user.setRoles("ROLE_USER");
            user.setPassword("PlaceholderPasswordToNotBreakTheModel");
            user.setPrivate(false);
            userModel = userRepository.save(user);
        }
        else {
            userModel = optionalUser.get();
        }
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(userModel.getRoles())), attributes, "email");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).map(SecurityUser::new).orElseThrow(() -> new UsernameNotFoundException("User not found: " +username));
    }

    @Override
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<UserModel> getUsersByName(String name) {
        return getAllUsers().stream().filter(userModel -> userModel.getUsername().contains(name)).toList();
    }

    public List<UserDto> getAllUserDtos() {
        return getAllUsers().stream().map(user -> new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImage(),
                user.getDescription(),
                user.getRoles(),
                user.isPrivate()
        )).toList();
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
    public Optional<UserDto> getUserDtoByUsername(String username) {
        List<UserModel> users = getAllUsers();
        for (UserModel user : users) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user).map(userToMap -> new UserDto(
                        userToMap.getId(),
                        userToMap.getUsername(),
                        userToMap.getEmail(),
                        userToMap.getProfileImage(),
                        userToMap.getDescription(),
                        userToMap.getRoles(),
                        user.isPrivate()));
            }
        }
        return Optional.empty();
    }

    public UserDto convertUserToDto(UserModel user) {
        return new UserDto(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImage(),
                user.getDescription(),
                user.getRoles(),
                user.isPrivate());
    }

    public Optional<UserDto> getUserDtoByUserId(Long userId) {
        return userRepository.findById(userId).map(
                user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getProfileImage(),
                        user.getDescription(),
                        user.getRoles(),
                        user.isPrivate()));
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
        user.setEmail(user.getEmail().toLowerCase());
        return userRepository.save(encryptPassword(user));
    }

    @Override
    public UserModel updateUser(UserModel user) {
        if(getUser(user.getId()).isEmpty()){
            return null;
        }
        if(!validateExistingUserUpdate(user)){
            return null;
        }
        else {
            user.setEmail(user.getEmail().toLowerCase());
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

    public Optional<FollowerModel> getFollowerModelById(FollowerModelId followerModelId) {
        return followerRepository.findById(followerModelId);
    }

    public List<FollowerModel> getAllFollowers() {
        return followerRepository.findAll();
    }

    public List<FollowerModel> getAllFollowersByUserId(Long userId) {
        return getAllFollowers().stream().filter(followerModel -> followerModel.getUser().getId().equals(userId)).toList();
    }

    public List<FollowerModel> getAllFollowingByUserId(Long userId) {
        return getAllFollowers().stream().filter(followerModel -> followerModel.getFollower().getId().equals(userId)).toList();
    }

    public Long getNumberOfFollowersByUserId(Long userId) {
        return getAllFollowers().stream().filter(followerModel -> followerModel.getUser().getId().equals(userId)).count();
    }

    public FollowerModel followNewUser(FollowerModel followerModel) {
        if(validateNewFollower(followerModel)){
            return followerRepository.save(followerModel);
        }
        return null;
    }

    public FollowerModel unfollowUser(Long userId, Long followerId) {
        FollowerModelId followerModelId = new FollowerModelId();
        followerModelId.setFollower_id(followerId);
        followerModelId.setUser_id(userId);
        if(getFollowerModelById(followerModelId).isPresent()){
            FollowerModel followerModel = getFollowerModelById(followerModelId).get();
            followerRepository.delete(followerModel);
            return followerModel;
        }
        else {
            throw new UserNotFoundException("Unable to unfollow user with id: " + userId + " by user id: " + followerId + "as follower relationship not found");
        }
    }

    public boolean validateNewFollower(FollowerModel followerModel) {
        if(getUser(followerModel.getFollower().getId()).isEmpty()){
            throw new UserNotFoundException(followerModel.getFollower().getId().toString());
        }
        if(getUser(followerModel.getUser().getId()).isEmpty()){
            throw new UserNotFoundException(followerModel.getUser().getId().toString());
        }
        if(followerModel.getFollower().getId().equals(followerModel.getUser().getId())){
            throw new InvalidUserException("You cannot follow yourself");
        }
        if(getFollowerModelById(followerModel.getId()).isPresent()){
            throw new InvalidUserException("You are already following user: " + followerModel.getUser().getUsername());
        }
        return true;
    }

    public boolean validateNewUser(UserModel user){
        if(getUser(user.getId()).isPresent()){
            throw new ModelAlreadyExistsException("Cannot create new User with ID: " + user.getId() + " already exists");
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
            throw new ModelAlreadyExistsException("Cannot update User as ID: " + user.getId() + " does not exist");
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
