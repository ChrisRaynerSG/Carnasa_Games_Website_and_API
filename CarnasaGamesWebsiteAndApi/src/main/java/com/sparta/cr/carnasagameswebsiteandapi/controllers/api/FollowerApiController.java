package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.models.FollowerModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class FollowerApiController {

    private final UserServiceImpl userService;

    @Autowired
    public FollowerApiController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/search/id/{userId}/following")
    public ResponseEntity<CollectionModel<EntityModel<FollowerModel>>> getAllFollowingByUserId(@PathVariable("userId") Long userId){
        List<EntityModel<FollowerModel>> following = userService.getAllFollowingByUserId(userId).stream().map( followerModel ->
                getUserEntityModel(followerModel).add(getUserLink(followerModel))
        ).toList();
        return ResponseEntity.ok(CollectionModel.of(following));
    }

    @GetMapping("/search/id/{userId}/followers")
    public ResponseEntity<CollectionModel<EntityModel<FollowerModel>>> getAllFollowersByUserId(@PathVariable("userId") Long userId){
        List<EntityModel<FollowerModel>> followers = userService.getAllFollowersByUserId(userId).stream().map( followerModel ->
                getUserEntityModel(followerModel).add(getFollowerLink(followerModel))
        ).toList();
        return ResponseEntity.ok(CollectionModel.of(followers));
    }

    @GetMapping("/search/id/{userId}/followers/number")
    public ResponseEntity<Long> getFollowersNumberByUserId(@PathVariable("userId") Long userId){
        if(userService.getUser(userId).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userService.getNumberOfFollowersByUserId(userId));
    }

    @PostMapping("/new/follower")
    public ResponseEntity<EntityModel<FollowerModel>> followNewUser(@RequestBody FollowerModel followerModel){
        userService.validateNewFollower(followerModel);
        userService.followNewUser(followerModel);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/follower/{userId}/{followerId}")
    public ResponseEntity<EntityModel<FollowerModel>> deleteFollower(@PathVariable Long userId, @PathVariable Long followerId){
        if(userService.getUser(userId).isEmpty()||userService.getUser(followerId).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.unfollowUser(userId, followerId);
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
