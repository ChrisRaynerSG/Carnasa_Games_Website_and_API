package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<CollectionModel<EntityModel<UserModel>>> getAllUsers() {
        List<EntityModel<UserModel>> allUsers = userService.getAllUsers().stream().map(this::getUserEntityModel).toList();
        return new ResponseEntity<>(CollectionModel.of(allUsers,WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getAllUsers())
                .withSelfRel()), HttpStatus.OK);
    }

    @GetMapping("/search/{userId}")
    public ResponseEntity<EntityModel<UserModel>> getUserById(@PathVariable Long userId) {
        if(userService.getUser(userId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        UserModel userModel = userService.getUser(userId).get();
        return new ResponseEntity<>(getUserEntityModel(userModel),HttpStatus.OK);
    }

    private List<Link> getCommentsLinks(UserModel user) {
        return commentService
                .getCommentsByUser(user.getId())
                .stream()
                .map(comment ->
                        WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(CommentController.class).getCommentById(comment.getId()))
                        .withRel(comment.getId().toString()))
                .toList();
    }

    private List<Link> getGamesLinks(UserModel user) {
        return gameService
                .getGamesByCreatorId(user.getId())
                .stream()
                .map(game ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(GameController.class).getGameById(game.getId()))
                                .withRel(game.getId().toString()))
                .toList();
    }

    private List<Link> getScoresLinks(UserModel user) {
        return highScoreService.getHighScoresByUser(user.getId())
                .stream()
                .map(score ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(HighScoreController.class).getScoreById(score.getScoreId()))
                                .withRel("Score: " + score.getScore() + " Game: " + score.getGamesModel().getTitle()))
                .toList();
    }

    private EntityModel<UserModel> getUserEntityModel(UserModel userModel) {
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(userModel.getId())).withSelfRel();
        return EntityModel.of(userModel, selfLink).add(getCommentsLinks(userModel)).add(getGamesLinks(userModel)).add(getScoresLinks(userModel));
    }
}
