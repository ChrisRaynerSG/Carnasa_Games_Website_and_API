package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.InvalidGenreException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UserNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UsernameNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
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
import java.util.regex.Matcher;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/carnasa-game-api/v1/games")
public class GameApiController {

    private final GameServiceImpl gameService;
    private final HighScoreServiceImpl highScoreService;
    private final CommentServiceImpl commentService;
    private final UserServiceImpl userService;

    @Autowired
    public GameApiController(GameServiceImpl gameService, HighScoreServiceImpl highScoreService, CommentServiceImpl commentService, UserServiceImpl userService) {
        this.gameService = gameService;
        this.highScoreService = highScoreService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/search/all")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getAllGames(){
        List<EntityModel<GameModel>> allGames = gameService.getAllGames()
                .stream()
                .map(this::getGameEntityModel).toList();
        return new ResponseEntity<>(CollectionModel.of(allGames).add(
                WebMvcLinkBuilder.linkTo(methodOn(GameApiController.class).getAllGames()).withSelfRel()
        ), HttpStatus.OK);
    }
    @GetMapping("/search/id/{gameId}")
    public ResponseEntity<EntityModel<GameModel>> getGameById(@PathVariable("gameId") Long gameId){
        if(gameService.getGame(gameId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        GameModel gameModel = gameService.getGame(gameId).get();
        return new ResponseEntity<>(getGameEntityModel(gameModel), HttpStatus.OK);
    }

    @GetMapping("/search/top10")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getTop10Games(){
        List<EntityModel<GameModel>> topTenGames = gameService.getTopTenGames().stream().map(
                this::getGameEntityModel
        ).toList();
        return new ResponseEntity<>
                (CollectionModel
                        .of(topTenGames)
                        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getTop10Games()).withSelfRel())
                        ,HttpStatus.OK);
    }

    @GetMapping("/search/top10/{genre}")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getTop10GamesByGenre(@PathVariable("genre") String genre){
        List<EntityModel<GameModel>> topTenGames = gameService.getTopTenGamesByGenre(genre).stream().map(
                this::getGameEntityModel
        ).toList();
        return new ResponseEntity<>
                (CollectionModel
                        .of(topTenGames)
                        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getTop10GamesByGenre(genre)).withSelfRel())
                        ,HttpStatus.OK);
    }

    @GetMapping("/search/all/user/name/{username}")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getAllUserGames(@PathVariable("username") String username){
        if (userService.getUserByUsername(username).isEmpty()){
            throw new UsernameNotFoundException(username);
        }
        List<EntityModel<GameModel>> gamesByUsername = gameService.getGamesByCreatorUsername(username).stream().map(
                this::getGameEntityModel
        ).toList();
        if(gamesByUsername.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>
                (CollectionModel
                        .of(gamesByUsername)
                        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getAllUserGames(username)).withSelfRel())
                        ,HttpStatus.OK);
    }

    @GetMapping("/search/all/user/id/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getAllUserGamesByUserId(@PathVariable("userId") Long userId){
        if(userService.getUser(userId).isEmpty()){
            throw new UserNotFoundException(userId.toString());
        }
        List<EntityModel<GameModel>> gamesByUserId = gameService.getGamesByCreatorId(userId).stream().map(
                this::getGameEntityModel
        ).toList();
        if(gamesByUserId.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>
                (CollectionModel
                        .of(gamesByUserId)
                        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getAllUserGamesByUserId(userId)).withSelfRel())
                        ,HttpStatus.OK);
    }

    @GetMapping("/search/title/{title}")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getAllGamesByTitle(@PathVariable("title") String title){
        List<EntityModel<GameModel>> gamesByPartialTitle = gameService.getGamesByTitle(title).stream().map(
                this::getGameEntityModel
        ).toList();
        if(gamesByPartialTitle.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>
                (CollectionModel
                        .of(gamesByPartialTitle)
                        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getAllGamesByTitle(title)).withSelfRel())
                        ,HttpStatus.OK);
    }

    @GetMapping("/search/genre/{genre}")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getAllGamesByGenre(@PathVariable("genre") String genre){
        Matcher matcher = gameService.getGenreMatcher(genre);
        if(!matcher.matches()){
            throw new InvalidGenreException(genre);
        }
        List<EntityModel<GameModel>> gamesByGenre = gameService.getGamesByGenre(genre).stream().map(
                this::getGameEntityModel
        ).toList();
        if(gamesByGenre.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>
                (CollectionModel
                        .of(gamesByGenre)
                        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getAllGamesByGenre(genre)).withSelfRel())
                        ,HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<EntityModel<GameModel>> createGame(@RequestBody GameModel gameModel){
        if(!gameService.validateNewGame(gameModel)){
            return ResponseEntity.badRequest().build();
        }
        GameModel newGame = gameService.createGame(gameModel);
        URI location = URI.create("/api/games/search/id/"+newGame.getId());
        Link selfLink = WebMvcLinkBuilder.linkTo(methodOn(GameApiController.class).getGameById(newGame.getId())).withSelfRel();
        return ResponseEntity.created(location).body(getGameEntityModel(newGame).add(selfLink));
    }

    @PutMapping("/id/{gameId}")
    public ResponseEntity updateGame(@PathVariable("gameId") Long gameId, @RequestBody GameModel gameModel){
        if(gameService.getGame(gameId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        if(!gameModel.getId().equals(gameId)){
            return ResponseEntity.badRequest().build();
        }
        if(!gameService.validateExistingGame(gameModel)){
            return ResponseEntity.badRequest().build();
        }
        gameService.updateGame(gameModel);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{gameId}")
    public ResponseEntity deleteGame(@PathVariable("gameId") Long gameId){
        if(gameService.getGame(gameId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }

    private Link getGameCreator(GameModel gameModel){
        return WebMvcLinkBuilder.linkTo(methodOn(UserApiController.class).getUserById(gameModel.getCreator().getId())).withRel("Creator: " + gameModel.getCreator().getUsername());
    }

    private List<Link> getGameScores(GameModel gameModel){
        return highScoreService.getHighScoresByGame(gameModel.getId())
                .stream()
                .map(score
                        -> WebMvcLinkBuilder
                        .linkTo(methodOn(HighScoreApiController.class).getScoreById(score.getScoreId()))
                        .withRel("Score: " + score.getScore() + " User: " + score.getUserModel().getUsername())).toList();

    }

    private List<Link> getGameComments(GameModel gameModel){
        return commentService.getCommentsByGame(gameModel.getId())
                .stream().map(comment ->
                        WebMvcLinkBuilder
                                .linkTo(methodOn(CommentApiController.class).getCommentById(comment.getId()))
                                .withRel("User: " + comment.getUserModel().getUsername() + " Comment: " +comment.getCommentText()))
                .toList();
    }

    private EntityModel<GameModel> getGameEntityModel(GameModel gameModel){
        Link selfLink = WebMvcLinkBuilder.linkTo(methodOn(GameApiController.class).getGameById(gameModel.getId())).withSelfRel();
        return EntityModel.of(gameModel, selfLink, getGameCreator(gameModel)).add(getGameScores(gameModel)).add(getGameComments(gameModel));

    }
}
