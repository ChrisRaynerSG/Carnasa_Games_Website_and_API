package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ForbiddenRoleException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.GenericUnauthorizedException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidUserException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UserNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UsernameNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.dtos.UserDto;
import com.sparta.cr.carnasagameswebsiteandapi.security.jwt.AnonymousAuthentication;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/carnasa-game-api/v1/users")
public class UserApiController {

    private final CommentServiceImpl commentService;
    private final UserServiceImpl userService;
    private final GameServiceImpl gameService;
    private final HighScoreServiceImpl highScoreService;
    private final FollowerServiceImpl followerService;
    private final FavouriteGameServiceImpl favouriteGameService;

    @Autowired
    public UserApiController(UserServiceImpl userService, CommentServiceImpl commentService,
                             GameServiceImpl gameService, HighScoreServiceImpl highScoreService,
                             FollowerServiceImpl followerService, FavouriteGameServiceImpl favouriteGameService) {
        this.userService = userService;
        this.commentService = commentService;
        this.gameService = gameService;
        this.highScoreService = highScoreService;
        this.followerService = followerService;
        this.favouriteGameService = favouriteGameService;
    }

    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAllUsers(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                                                             Authentication authentication) {
        if(authentication == null || !isRoleAdmin(authentication)){
            throw new ForbiddenRoleException();
        }
        else {
            List<EntityModel<UserDto>> allUsers = userService.getAllUsers(page,size).stream().map(
                    user -> getUserEntityModel(user, "Admin", authentication)
            ).toList();
            return new ResponseEntity<>(CollectionModel.of(allUsers,WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getAllUsers(page,size, authentication))
                    .withSelfRel()), HttpStatus.OK);
        }
    }

    @GetMapping("/search/id/{userId}")
    public ResponseEntity<EntityModel<UserDto>> getUserById(@PathVariable Long userId, Authentication authentication) {

        Authentication finalAuthentication = AnonymousAuthentication.ensureAuthentication(authentication);
        // if user private hide unless user OR admin
        if(userService.getUser(userId).isEmpty()) {
            throw new UserNotFoundException(userId.toString());
        }
        if(isAccountVisible(userId, finalAuthentication.getName(), finalAuthentication)) {
            UserModel userModel = userService.getUser(userId).get();
            return new ResponseEntity<>(getUserEntityModel(userModel, finalAuthentication.getName(), finalAuthentication),HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/search/name/{username}")
    public ResponseEntity<EntityModel<UserDto>> getUserByName(@PathVariable String username, Authentication authentication ) {

        Authentication finalAuthentication = AnonymousAuthentication.ensureAuthentication(authentication);

        if(userService.getUserByUsername(username).isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        UserModel userModel = userService.getUserByUsername(username).get();
        return new ResponseEntity<>(getUserEntityModel(userModel, finalAuthentication.getName(), finalAuthentication),HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<EntityModel<UserDto>> createUser(@RequestBody UserModel userModel, Authentication authentication) {

        Authentication finalAuthentication = AnonymousAuthentication.ensureAuthentication(authentication);

        if(!userService.validateNewUser(userModel)){
            return ResponseEntity.badRequest().build();
        }
        UserModel newUser = userService.createUser(userModel);
        URI location = URI.create("/api/users/search/id/"+newUser.getId());
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(newUser.getId(), finalAuthentication)).withSelfRel();
        return ResponseEntity.created(location).body(EntityModel.of(userService.convertUserToDto(newUser)).add(selfLink));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<EntityModel<UserModel>> updateUser(@PathVariable Long userId,
                                                             @RequestBody UserModel userModel,
                                                             Authentication authentication) {
        if(authentication == null){
            throw new GenericUnauthorizedException("Please login as Admin or user: " + userId + " to update user." );
        }
        if(userService.getUser(userId).isEmpty()) {
            throw new UserNotFoundException(userId.toString());
        }
        if(!userModel.getId().equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        if(isRoleAdmin(authentication) ||authentication.getName().equals(userService.getUser(userId).get().getUsername())){
            userService.validateExistingUserUpdate(userModel);
            userService.updateUser(userModel);
            return ResponseEntity.noContent().build();
        }
        else throw new ForbiddenRoleException();
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<EntityModel<UserModel>> deleteUser(@PathVariable Long userId, Authentication authentication) {

        if(authentication == null){
            throw new GenericUnauthorizedException("Please login as Admin or user: " + userId + " to delete user." );
        }
        if(userService.getUser(userId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else {
            if(isRoleAdmin(authentication)||authentication.getName().equals(userService.getUser(userId).get().getUsername())) {
                userService.deleteUser(userId);
                return ResponseEntity.noContent().build();
            }
            throw new ForbiddenRoleException();
        }
    }

    private List<Link> getCommentsLinks(UserModel user, String currentOwner, Authentication authentication) {
        return commentService
                .getCommentsByUser(user.getId())
                .stream()
                .map(comment ->
                        WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(CommentApiController.class).getCommentById(comment.getId(), authentication))
                        .withRel("Comment: " + comment.getCommentText()))
                .toList();
    }

    private List<Link> getGamesLinks(UserModel user, Authentication authentication) {
        return gameService
                .getGamesByCreatorId(user.getId(),0,10)
                .stream()
                .map(game ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getGameById(game.getId(), authentication))
                                .withRel("Game: " + game.getTitle()))
                .toList();
    }

    private List<Link> getScoresLinks(UserModel user, Authentication authentication) {
        return highScoreService.getHighScoresByUser(user.getId())
                .stream()
                .map(score ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getGameById(score.getGamesModel().getId(), authentication))
                                .withRel("Score: " + score.getScore() + " Game: " + score.getGamesModel().getTitle()))
                .toList();
    }

    private List<Link> getFollowersLinks(UserModel user, Authentication authentication) {
        return followerService.getAllFollowersByUserId(user.getId())
                .stream()
                .map(follower ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(follower.getFollower().getId(), authentication)).withRel(
                                        "Follower: " + follower.getFollower().getUsername())
                                ).toList();
    }
    private List<Link> getFollowingLinks(UserModel user, Authentication authentication) {
        return followerService.getAllFollowingByUserId(user.getId())
                .stream()
                .map(follower ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(follower.getUser().getId(), authentication)).withRel(
                                        "Following: " + follower.getUser().getUsername())
                ).toList();
    }
    private List<Link> getFavouriteGamesLinks(UserModel user, Authentication authentication) {
        return favouriteGameService.getAllFavouriteGamesByUserId(user.getId())
                .stream()
                .map(fav -> WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getGameById(fav.getGameModel().getId(), authentication)).withRel(
                        "Favourited Game: " + fav.getGameModel().getTitle())
                ).toList();
    }

    private EntityModel<UserDto> getUserEntityModel(UserModel userModel, String currentOwner, Authentication authentication) {
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(userModel.getId(), authentication)).withSelfRel();
        return EntityModel.of(userService.convertUserToDto(userModel), selfLink)
                .add(getCommentsLinks(userModel, currentOwner, authentication))
                .add(getGamesLinks(userModel, authentication))
                .add(getFavouriteGamesLinks(userModel, authentication))
                .add(getScoresLinks(userModel, authentication))
                .add(getFollowersLinks(userModel, authentication))
                .add(getFollowingLinks(userModel, authentication));
    }

    private boolean isRoleAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isAccountVisible(Long userId, String username, Authentication authentication) {
        return (userService.getUser(userId).get().isPrivate() && (isRoleAdmin(authentication) || username.equals(userService.getUser(userId).get().getUsername()))) || !userService.getUser(userId).get().isPrivate();
    }
}
