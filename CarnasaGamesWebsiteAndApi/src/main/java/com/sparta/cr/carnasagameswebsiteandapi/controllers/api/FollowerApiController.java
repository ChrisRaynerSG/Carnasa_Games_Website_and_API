package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.models.FollowerModel;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.FollowerServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carnasa-game-api/1.0/users")
public class FollowerApiController {

    private final FollowerServiceImpl followerService;
    private final UserServiceImpl userService;

    @Autowired
    public FollowerApiController(FollowerServiceImpl followerService, UserServiceImpl userService) {
        this.followerService = followerService;
        this.userService = userService;
    }

    @GetMapping("/search/id/{userId}/following")
    public ResponseEntity<CollectionModel<EntityModel<FollowerModel>>> getAllFollowingByUserId(@PathVariable("userId") Long userId){
        List<EntityModel<FollowerModel>> following = followerService.getAllFollowingByUserId(userId).stream().map( followerModel ->
                getUserEntityModel(followerModel).add(getUserLink(followerModel))
        ).toList();
        return ResponseEntity.ok(CollectionModel.of(following).add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(userId)).withSelfRel()));
    }

    @GetMapping("/search/id/{userId}/followers")
    public ResponseEntity<CollectionModel<EntityModel<FollowerModel>>> getAllFollowersByUserId(@PathVariable("userId") Long userId){
        List<EntityModel<FollowerModel>> followers = followerService.getAllFollowersByUserId(userId).stream().map( followerModel ->
                getUserEntityModel(followerModel).add(getFollowerLink(followerModel))
        ).toList();
        return ResponseEntity.ok(CollectionModel.of(followers).add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(userId)).withSelfRel()));
    }

    @GetMapping("/search/id/{userId}/followers/number")
    public ResponseEntity<Long> getFollowersNumberByUserId(@PathVariable("userId") Long userId){
        if(userService.getUser(userId).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(followerService.getNumberOfFollowersByUserId(userId));
    }

    @PostMapping("/new/follower")
    public ResponseEntity<EntityModel<FollowerModel>> followNewUser(@RequestBody FollowerModel followerModel){
        followerService.validateNewFollower(followerModel);
        followerService.followNewUser(followerModel);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/follower/{userId}/{followerId}")
    public ResponseEntity<EntityModel<FollowerModel>> deleteFollower(@PathVariable Long userId, @PathVariable Long followerId){
        if(userService.getUser(userId).isEmpty()||userService.getUser(followerId).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        followerService.unfollowUser(userId, followerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private Link getUserLink(FollowerModel followerModel) {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(followerModel.getUser().getId())).withRel("Following: " + followerModel.getUser().getUsername());
    }
    private Link getFollowerLink(FollowerModel followerModel) {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(followerModel.getFollower().getId())).withRel("Follower: " + followerModel.getFollower().getUsername());
    }

    private EntityModel<FollowerModel> getUserEntityModel(FollowerModel followerModel) {
        return EntityModel.of(followerModel);
    }
}
