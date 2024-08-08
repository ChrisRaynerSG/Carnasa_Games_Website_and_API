package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidUserException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UserNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.FollowerModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.FollowerModelId;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.FollowerRepository;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FollowerServiceImpl {

    private UserServiceImpl userService;
    private FollowerRepository followerRepository;

    @Autowired
    public FollowerServiceImpl(FollowerRepository followerRepository, UserServiceImpl userService) {
        this.followerRepository = followerRepository;
        this.userService = userService;
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
        if(userService.getUser(followerModel.getFollower().getId()).isEmpty()){
            throw new UserNotFoundException(followerModel.getFollower().getId().toString());
        }
        if(userService.getUser(followerModel.getUser().getId()).isEmpty()){
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
}
