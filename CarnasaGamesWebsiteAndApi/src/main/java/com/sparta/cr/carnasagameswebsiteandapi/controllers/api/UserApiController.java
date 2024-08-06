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

    private final CommentServiceImpl commentServiceImpl;
    private final UserServiceImpl userService;
    private final UserServiceImpl userServiceImpl;
    private final GameServiceImpl gameServiceImpl;
    private final HighScoreServiceImpl highScoreServiceImpl;

    @Autowired
    public UserApiController(UserServiceImpl userService, CommentServiceImpl commentServiceImpl, UserServiceImpl userServiceImpl, GameServiceImpl gameServiceImpl, HighScoreServiceImpl highScoreServiceImpl) {
        this.userService = userService;
        this.commentServiceImpl = commentServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.gameServiceImpl = gameServiceImpl;
        this.highScoreServiceImpl = highScoreServiceImpl;
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
        EntityModel<UserModel> userEntityModel = EntityModel.of(userModel,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(userId)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getAllUsers()).withRel("All Users")
        );
        return new ResponseEntity<>(userEntityModel
                .add(getCommentsLinks(userModel))
                .add(getGamesLinks(userModel))
                .add(getScoresLinks(userModel))
                ,HttpStatus.OK);
    }

    private List<Link> getCommentsLinks(UserModel user) {
        return commentServiceImpl
                .getCommentsByUser(user.getId())
                .stream()
                .map(comment ->
                        WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(CommentController.class).getComment(comment.getId()))
                        .withRel(comment.getId().toString()))
                .toList();
    }

    private List<Link> getGamesLinks(UserModel user) {
        return gameServiceImpl
                .getGamesByCreatorId(user.getId())
                .stream()
                .map(game ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(GameController.class).getGame(game.getId()))
                                .withRel(game.getId().toString()))
                .toList();
    }

    private List<Link> getScoresLinks(UserModel user) {
        return highScoreServiceImpl.getHighScoresByUser(user.getId())
                .stream()
                .map(score ->
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(HighScoreController.class).getScore(score.getScoreId()))
                                .withRel(score.getScoreId().toString()))
                .toList();
    }

    private EntityModel<UserModel> getUserEntityModel(UserModel userModel) {

        List<Link> commentsLinks = getCommentsLinks(userModel);
        List<Link> gamesLinks = getGamesLinks(userModel);
        List<Link> scoresLinks = getScoresLinks(userModel);

        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(userModel.getId())).withSelfRel();
        Link relink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getAllUsers()).withRel("All Users");
        return EntityModel.of(userModel, selfLink, relink).add(commentsLinks).add(gamesLinks).add(scoresLinks);
    }
}
