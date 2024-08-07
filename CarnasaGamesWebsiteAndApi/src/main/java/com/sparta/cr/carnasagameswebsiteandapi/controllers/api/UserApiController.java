package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UserNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UsernameNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.dtos.UserDto;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.CommentServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.GameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.HighScoreServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final CommentServiceImpl commentService;
    private final UserServiceImpl userService;
    private final GameServiceImpl gameService;
    private final HighScoreServiceImpl highScoreService;

    @Autowired
    public UserApiController(UserServiceImpl userService, CommentServiceImpl commentService, GameServiceImpl gameService, HighScoreServiceImpl highScoreService) {
        this.userService = userService;
        this.commentService = commentService;
        this.gameService = gameService;
        this.highScoreService = highScoreService;
    }

    @GetMapping("/search/all")
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAllUsers() {
        List<EntityModel<UserDto>> allUsers = userService.getAllUserDtos().stream().map(this::getUserEntityModel).toList();
        return new ResponseEntity<>(CollectionModel.of(allUsers,WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getAllUsers())
                .withSelfRel()), HttpStatus.OK);
    }

    @GetMapping("/search/id/{userId}")
    public ResponseEntity<EntityModel<UserDto>> getUserById(@PathVariable Long userId) {
        if(userService.getUserDtoByUserId(userId).isEmpty()) {
            throw new UserNotFoundException(userId.toString());
        }
        UserDto userDto = userService.getUserDtoByUserId(userId).get();
        return new ResponseEntity<>(getUserEntityModel(userDto),HttpStatus.OK);
    }

    @GetMapping("/search/name/{username}")
    public ResponseEntity<EntityModel<UserDto>> getUserByName(@PathVariable String username) {
        if(userService.getUserDtoByUsername(username).isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        UserDto userDto = userService.getUserDtoByUsername(username).get();
        return new ResponseEntity<>(getUserEntityModel(userDto),HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<EntityModel<UserDto>> createUser(@RequestBody UserModel userModel) {
        if(!userService.validateNewUser(userModel)){
            return ResponseEntity.badRequest().build();
        }
        UserModel newUser = userService.createUser(userModel);
        URI location = URI.create("/api/users/search/id/"+newUser.getId());
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(newUser.getId())).withSelfRel();
        return ResponseEntity.created(location).body(EntityModel.of(userService.convertUserToDto(newUser)).add(selfLink));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<EntityModel<UserModel>> updateUser(@PathVariable Long userId, @RequestBody UserModel userModel) {
        if(userService.getUser(userId).isEmpty()) {
            throw new UserNotFoundException(userId.toString());
        }
        if(!userModel.getId().equals(userId)) {
            return ResponseEntity.badRequest().build();
        }

        userService.validateExistingUserUpdate(userModel);
        userService.updateUser(userModel);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<EntityModel<UserModel>> deleteUser(@PathVariable Long userId) {
        if(userService.getUser(userId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    private List<Link> getCommentsLinks(UserDto user) {
        return commentService
                .getCommentsByUser(user.getId())
                .stream()
                .map(comment ->
                        WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(CommentController.class).getCommentById(comment.getId()))
                        .withRel("Comment: " + comment.getCommentText()))
                .toList();
    }

    private List<Link> getGamesLinks(UserDto user) {
        return gameService
                .getGamesByCreatorId(user.getId())
                .stream()
                .map(game ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(GameController.class).getGameById(game.getId()))
                                .withRel("Game: " + game.getTitle()))
                .toList();
    }

    private List<Link> getScoresLinks(UserDto user) {
        return highScoreService.getHighScoresByUser(user.getId())
                .stream()
                .map(score ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(GameController.class).getGameById(score.getGamesModel().getId()))
                                .withRel("Score: " + score.getScore() + " Game: " + score.getGamesModel().getTitle()))
                .toList();
    }

    private List<Link> getFollowersLinks(UserDto user) {
        return userService.getAllFollowersByUserId(user.getId())
                .stream()
                .map(follower ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(follower.getFollower().getId())).withRel(
                                        "Follower: " + follower.getFollower().getUsername())
                                ).toList();
    }
    private List<Link> getFollowingLinks(UserDto user) {
        return userService.getAllFollowingByUserId(user.getId())
                .stream()
                .map(follower ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(follower.getUser().getId())).withRel(
                                        "Following: " + follower.getUser().getUsername())
                ).toList();
    }

    private EntityModel<UserDto> getUserEntityModel(UserDto userModel) {
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(userModel.getId())).withSelfRel();
        return EntityModel.of(userModel, selfLink).add(getCommentsLinks(userModel)).add(getGamesLinks(userModel)).add(getScoresLinks(userModel)).add(getFollowersLinks(userModel)).add(getFollowingLinks(userModel));
    }
}
