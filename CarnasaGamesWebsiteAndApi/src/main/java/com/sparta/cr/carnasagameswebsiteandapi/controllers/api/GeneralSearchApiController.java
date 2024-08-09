package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.models.CommentModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.FollowerModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.dtos.UserDto;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.*;
import io.swagger.v3.oas.annotations.headers.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carnasa-game-api/v1/search")
public class GeneralSearchApiController {

    private final CommentServiceImpl commentService;
    private final GameServiceImpl gameService;
    private final UserServiceImpl userService;

    @Autowired
    public GeneralSearchApiController(CommentServiceImpl commentService,
                                      GameServiceImpl gameService,
                                      UserServiceImpl userService){
        this.commentService = commentService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<Object>>> getAllDbItems() {
        List<EntityModel<Object>> allUsers = userService.getAllUsers(0,10).stream()
                .map(user -> EntityModel.of((Object) userService.convertUserToDto(user)))
                .toList();

        List<EntityModel<Object>> allGames = gameService.getAllGames(0,10).stream()
                .map(game -> EntityModel.of((Object) game))
                .toList();

        List<EntityModel<Object>> allComments = commentService.getAllComments().stream()
                .map(comment -> EntityModel.of((Object) comment))
                .toList();

        List<EntityModel<Object>> allResults = new ArrayList<>();
        allResults.addAll(allUsers);
        allResults.addAll(allGames);
        allResults.addAll(allComments);

        CollectionModel<EntityModel<Object>> collectionModel = CollectionModel.of(allResults);
        return ResponseEntity.ok(collectionModel);
    }
    @GetMapping("/{searchQuery}")
    public ResponseEntity<CollectionModel<EntityModel<Object>>> getAllDbItemsByPartialMatch(@PathVariable String searchQuery) {
        List<EntityModel<Object>> allUsersByUsername = userService.getUsersByName(searchQuery,0,10).stream()
                .map(user -> EntityModel.of((Object) userService.convertUserToDto(user)))
                .toList();
        List<EntityModel<Object>> allGamesByTitle = gameService.getGamesByTitle(searchQuery, 0,10).stream()
                .map(game -> EntityModel.of((Object) game))
                .toList();
        List<EntityModel<Object>> allCommentsByText = commentService.getCommentsByTextPartialMatch(searchQuery).stream()
                .map(comment -> EntityModel.of((Object) comment))
                .toList();
        List<EntityModel<Object>> allCommentsByUser = commentService.getCommentsByUsernamePartialMatch(searchQuery).stream()
                .map(comment -> EntityModel.of((Object) comment))
                .toList();
        List<EntityModel<Object>> allResults = new ArrayList<>();
        allResults.addAll(allUsersByUsername);
        allResults.addAll(allGamesByTitle);
        allResults.addAll(allCommentsByText);
        allResults.addAll(allCommentsByUser);
        //all games by genre?

        CollectionModel<EntityModel<Object>> collectionModel = CollectionModel.of(allResults);
        return ResponseEntity.ok(collectionModel);
    }
}
