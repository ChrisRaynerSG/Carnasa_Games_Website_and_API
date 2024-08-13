package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.annotations.CurrentOwner;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.GenericUnauthorizedException;
import com.sparta.cr.carnasagameswebsiteandapi.models.FollowerModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.security.jwt.AnonymousAuthentication;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.FollowerServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carnasa-game-api/v1/users")
public class FollowerApiController {

    private final FollowerServiceImpl followerService;
    private final UserServiceImpl userService;

    @Autowired
    public FollowerApiController(FollowerServiceImpl followerService, UserServiceImpl userService) {
        this.followerService = followerService;
        this.userService = userService;
    }

    @GetMapping("/search/id/{userId}/following")
    public ResponseEntity<CollectionModel<EntityModel<FollowerModel>>> getAllFollowingByUserId(@PathVariable("userId") Long userId,
                                                                                               Authentication authentication){
        Authentication finalAuthentication = AnonymousAuthentication.ensureAuthentication(authentication);
        List<EntityModel<FollowerModel>> following = followerService.getAllFollowingByUserId(userId).stream().map( followerModel ->
                getUserEntityModel(followerModel).add(getUserLink(followerModel, finalAuthentication.getName(), finalAuthentication))
        ).toList();
        return ResponseEntity.ok(CollectionModel.of(following).add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(userId, authentication)).withSelfRel()));
    }

    @GetMapping("/search/id/{userId}/followers")
    public ResponseEntity<CollectionModel<EntityModel<FollowerModel>>> getAllFollowersByUserId(@PathVariable("userId") Long userId,
                                                                                               Authentication authentication){
        Authentication finalAuthentication = AnonymousAuthentication.ensureAuthentication(authentication);
        List<EntityModel<FollowerModel>> followers = followerService.getAllFollowersByUserId(userId).stream().map( followerModel ->
                getUserEntityModel(followerModel).add(getFollowerLink(followerModel, finalAuthentication.getName(), finalAuthentication))
        ).toList();
        return ResponseEntity.ok(CollectionModel.of(followers).add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(userId,authentication)).withSelfRel()));
    }

    @GetMapping("/search/id/{userId}/followers/number")
    public ResponseEntity<Long> getFollowersNumberByUserId(@PathVariable("userId") Long userId){
        if(userService.getUser(userId).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(followerService.getNumberOfFollowersByUserId(userId));
    }

    @PostMapping("/new/follower")
    public ResponseEntity<EntityModel<FollowerModel>> followNewUser(@RequestBody FollowerModel followerModel, Authentication authentication){
        if(authentication == null){
            throw new GenericUnauthorizedException("Please login first to follow user");
        }
        followerService.validateNewFollower(followerModel);
        followerService.followNewUser(followerModel);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/follower/{userId}/{followerId}")
    public ResponseEntity<EntityModel<FollowerModel>> deleteFollower(@PathVariable Long userId, @PathVariable Long followerId, Authentication authentication){

        if(authentication == null){
            throw new GenericUnauthorizedException("Please login first to unfollow user");
        }

        if(userService.getUser(userId).isEmpty()||userService.getUser(followerId).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        followerService.unfollowUser(userId, followerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private Link getUserLink(FollowerModel followerModel, String currentUser, Authentication authentication) {
        if(isUserPrivateAndNotVisible(authentication, currentUser, followerModel)){
            return Link.of("/").withRel("Private user");
        }
        else {
            return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(followerModel.getUser().getId(), authentication)).withRel("Following: " + followerModel.getUser().getUsername());
        }
    }
    private Link getFollowerLink(FollowerModel followerModel, String currentUser, Authentication authentication) {
        if(isFollowerPrivateAndNotVisible(authentication, currentUser, followerModel)){
            return Link.of("/").withRel("Private user");
        }
        else {
            return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(followerModel.getFollower().getId(),authentication)).withRel("Follower: " + followerModel.getFollower().getUsername());
        }
    }

    private EntityModel<FollowerModel> getUserEntityModel(FollowerModel followerModel) {
        return EntityModel.of(followerModel);
    }
    private boolean isUserPrivateAndNotVisible(Authentication authentication, String currentUser, FollowerModel followerModel) {
        return followerModel.getUser().isPrivate()
                && authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))
                && !followerModel.getUser().getUsername().equals(currentUser);
    }
    private boolean isFollowerPrivateAndNotVisible(Authentication authentication, String currentUser, FollowerModel followerModel) {
        return followerModel.getFollower().isPrivate()
                && authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))
                && !followerModel.getFollower().getUsername().equals(currentUser);
    }
    //todo update post and delete methods so only admins or current user can change
}
