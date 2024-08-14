package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.InvalidGenreException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.GenericUnauthorizedException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UserNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
import com.sparta.cr.carnasagameswebsiteandapi.security.jwt.AnonymousAuthentication;
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
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getAllGames(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                                                               Authentication authentication){
        Authentication finalAuthentication = AnonymousAuthentication.ensureAuthentication(authentication);
        List<EntityModel<GameModel>> allGames = gameService.getAllGames(page, size)
                .stream()
                .map(
                        game -> getGameEntityModel(game, finalAuthentication.getName(), finalAuthentication)
                ).toList();
        return new ResponseEntity<>(CollectionModel.of(allGames).add(
                WebMvcLinkBuilder.linkTo(methodOn(GameApiController.class).getAllGames(page, size, finalAuthentication)).withSelfRel()
        ), HttpStatus.OK);
    }
    @GetMapping("/search/id/{gameId}")
    public ResponseEntity<EntityModel<GameModel>> getGameById(@PathVariable("gameId") Long gameId,
                                                              Authentication authentication){
        Authentication finalAuthentication = AnonymousAuthentication.ensureAuthentication(authentication);
        if(gameService.getGame(gameId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        GameModel gameModel = gameService.getGame(gameId).get();
        return new ResponseEntity<>(getGameEntityModel(gameModel, finalAuthentication.getName(), finalAuthentication), HttpStatus.OK);
    }

    @GetMapping("/search/top10")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getTop10Games(Authentication authentication){
        Authentication finalAuthentication = AnonymousAuthentication.ensureAuthentication(authentication);
        List<EntityModel<GameModel>> topTenGames = gameService.getTopTenGames().stream().map(
                game -> getGameEntityModel(game, finalAuthentication.getName(), finalAuthentication)
        ).toList();
        return new ResponseEntity<>
                (CollectionModel
                        .of(topTenGames)
                        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getTop10Games(finalAuthentication)).withSelfRel())
                        ,HttpStatus.OK);
    }

    @GetMapping("/search/top10/{genre}")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getTop10GamesByGenre(@PathVariable("genre") String genre,Authentication authentication){
        Authentication finalAuthentication = AnonymousAuthentication.ensureAuthentication(authentication);
        List<EntityModel<GameModel>> topTenGames = gameService.getTopTenGamesByGenre(genre).stream().map(
                game -> getGameEntityModel(game, finalAuthentication.getName(), finalAuthentication)
        ).toList();
        return new ResponseEntity<>
                (CollectionModel
                        .of(topTenGames)
                        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getTop10GamesByGenre(genre, authentication)).withSelfRel())
                        ,HttpStatus.OK);
    }

    @GetMapping("/search/all/user/name/{username}")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getAllUserGames(@PathVariable("username") String username,
                                                                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                   Authentication authentication){
        Authentication finalAuthentication = AnonymousAuthentication.ensureAuthentication(authentication);
        List<EntityModel<GameModel>> gamesByUsername = gameService
                .getGamesByCreatorUsername(username,page,size).stream().map(
                game -> getGameEntityModel(game, finalAuthentication.getName(), finalAuthentication)
        ).toList();
        if(gamesByUsername.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        CollectionModel<EntityModel<GameModel>> collectionModel = CollectionModel.of(gamesByUsername);
        collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class)
                .getAllUserGames(username, page, size,finalAuthentication)).withSelfRel());
        if (page > 0) {
            collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class)
                    .getAllUserGames(username, page - 1, size,finalAuthentication)).withRel("previous"));
        }
        collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class)
                .getAllUserGames(username, page + 1, size,finalAuthentication)).withRel("next"));
        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    @GetMapping("/search/all/user/id/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getAllUserGamesByUserId(@PathVariable("userId") Long userId,
                                                                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                           @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                           Authentication authentication){

        Authentication finalAuthentication = AnonymousAuthentication.ensureAuthentication(authentication);
        if(userService.getUser(userId).isEmpty()){
            throw new UserNotFoundException(userId.toString());
        }
        List<EntityModel<GameModel>> gamesByUserId = gameService.getGamesByCreatorId(userId, page, size).stream().map(
                game -> getGameEntityModel(game, finalAuthentication.getName(), finalAuthentication)
        ).toList();
        if(gamesByUserId.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>
                (CollectionModel
                        .of(gamesByUserId)
                        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getAllUserGamesByUserId(userId, page, size, finalAuthentication)).withSelfRel())
                        ,HttpStatus.OK);
    }

    @GetMapping("/search/title/{title}")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getAllGamesByTitle(@PathVariable("title") String title,
                                                                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                      Authentication authentication){
        List<EntityModel<GameModel>> gamesByPartialTitle = gameService.getGamesByTitle(title, page, size).stream().map(
                game -> getGameEntityModel(game, authentication.getName(), authentication)
        ).toList();
        if(gamesByPartialTitle.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>
                (CollectionModel
                        .of(gamesByPartialTitle)
                        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getAllGamesByTitle(title, page, size,authentication)).withSelfRel())
                        ,HttpStatus.OK);
    }

    @GetMapping("/search/genre/{genre}")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getAllGamesByGenre(@PathVariable("genre") String genre,
                                                                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                      Authentication authentication){
        Matcher matcher = gameService.getGenreMatcher(genre);
        if(!matcher.matches()){
            throw new InvalidGenreException(genre);
        }
        List<EntityModel<GameModel>> gamesByGenre = gameService.getGamesByGenre(genre,page,size).stream().map(
                game -> getGameEntityModel(game, authentication.getName(), authentication)
        ).toList();
        if(gamesByGenre.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>
                (CollectionModel
                        .of(gamesByGenre)
                        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getAllGamesByGenre(genre, page, size,authentication)).withSelfRel())
                        ,HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<EntityModel<GameModel>> createGame(@RequestBody GameModel gameModel,
                                                             Authentication authentication){
        if(authentication==null){
            throw new GenericUnauthorizedException("Please login before creating a new game");
        }
        if(!gameService.validateNewGame(gameModel)){
            return ResponseEntity.badRequest().build();
        }
        if(userService.getUserByUsername(authentication.getName()).isEmpty()){
            throw new ModelNotFoundException("User not found");
        }
        gameModel.setCreator(userService.getUserByUsername(authentication.getName()).get());
        GameModel newGame = gameService.createGame(gameModel);
        URI location = URI.create("/api/games/search/id/"+newGame.getId());
        Link selfLink = WebMvcLinkBuilder.linkTo(methodOn(GameApiController.class).getGameById(newGame.getId(),authentication)).withSelfRel();
        return ResponseEntity.created(location).body(getGameEntityModel(newGame, authentication.getName(), authentication).add(selfLink));
    }

    @PutMapping("/id/{gameId}")
    public ResponseEntity updateGame(@PathVariable("gameId") Long gameId,
                                     @RequestBody GameModel gameModel,
                                     Authentication authentication){
        if(authentication==null){
            throw new GenericUnauthorizedException("Please login as admin or user: " + gameModel.getCreator().getId() + " before updating this game.");
        }
        if(gameService.getGame(gameId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        if(!gameModel.getId().equals(gameId)){
            return ResponseEntity.badRequest().build();
        }
        if(!gameService.validateExistingGame(gameModel)){
            return ResponseEntity.badRequest().build();
        }
        if(userService.getUserByUsername(authentication.getName()).isEmpty()){//shouldn't ever happen but I dont like yellow squigglies
            throw new ModelNotFoundException("User not found");
        }
        if(isRoleAdmin(authentication)||gameModel.getCreator().getUsername().equals(userService.getUserByUsername(authentication.getName()).get().getUsername())){
            if(!isRoleAdmin(authentication)){
                gameModel.setTimesPlayed(gameService.getGame(gameId).get().getTimesPlayed()); //ensure playtime not updated
            }
            gameService.updateGame(gameModel);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can not update another users game.");
        }
    }

    @PatchMapping("/id/{gameId}/increase-play-count")
    public ResponseEntity increasePlayCount(@PathVariable("gameId") Long gameId){
        if(gameService.getGame(gameId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        GameModel gameModel = gameService.getGame(gameId).get();
        gameService.increasePlaysByOne(gameModel);
        return ResponseEntity.noContent().build();
    }

    //patch mapping for updating if published?

    @DeleteMapping("/delete/{gameId}")
    public ResponseEntity deleteGame(@PathVariable("gameId") Long gameId,
                                     Authentication authentication){
        if(authentication==null){
            if(gameService.getGame(gameId).isEmpty()){
                return ResponseEntity.notFound().build();
            }
            throw new GenericUnauthorizedException("Please login as admin or user: " + gameService.getGame(gameId).get().getCreator().getId() + " before deleting this game.");
        }
        // can only delete if game creator or admin
        if(gameService.getGame(gameId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }

    private Link getGameCreator(GameModel gameModel, String currentOwner, Authentication authentication){
        if(isUserFromGamePrivateAndNotVisible(authentication,currentOwner,gameModel)){
            return Link.of("/").withRel("Creator is private");
        }
        else {
            return WebMvcLinkBuilder.linkTo(methodOn(UserApiController.class).getUserById(gameModel.getCreator().getId(), authentication)).withRel("Creator: " + gameModel.getCreator().getUsername());
        }
    }

    private List<Link> getGameScores(GameModel gameModel, String currentUser, Authentication authentication){
        return highScoreService.getHighScoresByGame(gameModel.getId())
                .stream()
                .map(score
                        -> {
                    if(isUserFromScorePrivateAndNotVisible(authentication, currentUser, score)){
                        return WebMvcLinkBuilder
                                .linkTo(methodOn(HighScoreApiController.class).getScoreById(score.getScoreId(),authentication))
                                .withRel("Private user - Score: " + score.getScore());
                    }
                    else{
                        return WebMvcLinkBuilder
                                .linkTo(methodOn(HighScoreApiController.class).getScoreById(score.getScoreId(),authentication))
                                .withRel(" User: " + score.getUserModel().getUsername() + " Score: " + score.getScore());
                        }
                }).toList();
    }

    private boolean isUserFromScorePrivateAndNotVisible(Authentication authentication, String currentUser, HighScoreModel score) {
        return userService.getUser(score.getUserModel().getId()).get().isPrivate()
                && authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))
                && !userService.getUser(score.getUserModel().getId()).get().getUsername().equals(currentUser);
    }
    private boolean isUserFromGamePrivateAndNotVisible(Authentication authentication, String currentUser, GameModel game) {
        return userService.getUser(game.getCreator().getId()).get().isPrivate()
                && authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))
                && !userService.getUser(game.getCreator().getId()).get().getUsername().equals(currentUser);
    }

    private List<Link> getGameComments(GameModel gameModel, Authentication authentication){
        return commentService.getCommentsByGame(gameModel.getId())
                .stream().map(comment ->
                        WebMvcLinkBuilder
                                .linkTo(methodOn(CommentApiController.class).getCommentById(comment.getId(), authentication))
                                .withRel("User: " + comment.getUserModel().getUsername() + " Comment: " +comment.getCommentText()))
                .toList();
    }

    private EntityModel<GameModel> getGameEntityModel(GameModel gameModel, String currentUser, Authentication authentication){
        Link selfLink = WebMvcLinkBuilder.linkTo(methodOn(GameApiController.class).getGameById(gameModel.getId(),authentication)).withSelfRel();
        return EntityModel.of(gameModel, selfLink, getGameCreator(gameModel, currentUser, authentication)).add(getGameScores(gameModel, currentUser, authentication)).add(getGameComments(gameModel, authentication));
    }
    private boolean isRoleAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
    }
}
