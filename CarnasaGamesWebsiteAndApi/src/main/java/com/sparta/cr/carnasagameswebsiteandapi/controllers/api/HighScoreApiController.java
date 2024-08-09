package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
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
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/carnasa-game-api/v1/scores")
public class HighScoreApiController {

    private final HighScoreServiceImpl highScoreService;
    private final GameServiceImpl gameService;
    private final UserServiceImpl userService;

    @Autowired
    public HighScoreApiController(HighScoreServiceImpl highScoreService, GameServiceImpl gameService, UserServiceImpl userService){
        this.highScoreService = highScoreService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/search/all")
    public ResponseEntity<CollectionModel<EntityModel<HighScoreModel>>> getAllHighScores(){
        List<EntityModel<HighScoreModel>> highScores = highScoreService.getAllHighScores().stream().map(this::getHighScoreEntityModel).toList();
        return new ResponseEntity<>(CollectionModel.of(highScores).add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HighScoreApiController.class).getAllHighScores()).withSelfRel()
        ), HttpStatus.OK);
    }

    @GetMapping("/search/{scoreId}")
    public ResponseEntity<EntityModel<HighScoreModel>> getScoreById(@PathVariable Long scoreId){
        if(highScoreService.getHighScore(scoreId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        HighScoreModel highScoreModel = highScoreService.getHighScore(scoreId).get();
        return ResponseEntity.ok(getHighScoreEntityModel(highScoreModel).add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HighScoreApiController.class).getAllHighScores()).withRel("All Scores")
        ));
    }

    @GetMapping("/search/games/{gameId}")
    public ResponseEntity<CollectionModel<EntityModel<HighScoreModel>>> getHighScoresByGameId(@PathVariable Long gameId){
        if(gameService.getGame(gameId).isEmpty()){
            throw new ModelNotFoundException("Game with id " + gameId + " does not exist");
        }
        List<EntityModel<HighScoreModel>> highScores = highScoreService.getHighScoresByGame(gameId).stream().map(this::getHighScoreEntityModel).toList();
        if(highScores.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CollectionModel.of(highScores));
    }
    @GetMapping("/search/users/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<HighScoreModel>>> getHighScoresByUserId(@PathVariable Long userId){
        if(userService.getUser(userId).isEmpty()){
            throw new ModelNotFoundException("User with id " + userId + " does not exist");
        }
        List<EntityModel<HighScoreModel>> highScores = highScoreService.getHighScoresByUser(userId).stream().map(this::getHighScoreEntityModel).toList();
        if(highScores.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CollectionModel.of(highScores));
    }
    @GetMapping("/search/games/{gameId}/top10")
    public ResponseEntity<CollectionModel<EntityModel<HighScoreModel>>> getTop10HighScoresForGame(@PathVariable Long gameId){
        if(gameService.getGame(gameId).isEmpty()){
            throw new ModelNotFoundException("Game with id " + gameId + " does not exist");
        }
        List<EntityModel<HighScoreModel>> highScores = highScoreService.getTop10HighScoresByGame(gameId).stream().map(this::getHighScoreEntityModel).toList();
        if(highScores.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CollectionModel.of(highScores));
    }
    @GetMapping("/search/games/{gameId}/top10today")
    public ResponseEntity<CollectionModel<EntityModel<HighScoreModel>>> getTop10HighScoresForGameToday(@PathVariable Long gameId){
        if(gameService.getGame(gameId).isEmpty()){
            throw new ModelNotFoundException("Game with id " + gameId + " does not exist");
        }
        List<EntityModel<HighScoreModel>> highScores = highScoreService.getHighScoresToday(gameId, LocalDate.now()).stream().map(this::getHighScoreEntityModel).toList();
        if(highScores.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CollectionModel.of(highScores));
    }
    @GetMapping("/search/games/{gameId}/users/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<HighScoreModel>>> getHighScoresByUserIdForGame(@PathVariable Long gameId, @PathVariable Long userId){
        if(gameService.getGame(gameId).isEmpty()){
            throw new ModelNotFoundException("Game with id " + gameId + " does not exist");
        }
        if(userService.getUser(userId).isEmpty()){
            throw new ModelNotFoundException("User with id " + userId + " does not exist");
        }
        List<EntityModel<HighScoreModel>> highScores = highScoreService.getHighScoresByGameAndUser(userId, gameId).stream().map(this::getHighScoreEntityModel).toList();
        if(highScores.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CollectionModel.of(highScores));
    }

    @PostMapping("/new")
    public ResponseEntity<EntityModel<HighScoreModel>> createHighScore(@RequestBody HighScoreModel highScoreModel){
        highScoreService.validateNewHighScore(highScoreModel);
        HighScoreModel newHighScore = highScoreService.createHighScore(highScoreModel);
        URI location = URI.create("/api/scores/search/" + newHighScore.getScoreId());
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HighScoreApiController.class).getAllHighScores()).withSelfRel();
        return ResponseEntity.created(location).body(EntityModel.of(newHighScore).add(selfLink));
    }
    @PutMapping("/update/{scoreId}")
    public ResponseEntity<EntityModel<HighScoreModel>> updateHighScore(@PathVariable Long scoreId, @RequestBody HighScoreModel highScoreModel){
        if(highScoreService.getHighScore(scoreId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        if(!highScoreModel.getScoreId().equals(scoreId)){
            return ResponseEntity.badRequest().build();
        }
        highScoreService.validateExistingHighScore(highScoreModel);
        highScoreService.updateHighScore(highScoreModel);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{scoreId}")
    public ResponseEntity<EntityModel<HighScoreModel>> deleteHighScore(@PathVariable Long scoreId){
        if(highScoreService.getHighScore(scoreId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        highScoreService.deleteHighScore(scoreId);
        return ResponseEntity.noContent().build();
    }

    private Link getUserLink(HighScoreModel highScoreModel){
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(highScoreModel.getUserModel().getId())).withRel("User: " + highScoreModel.getUserModel().getUsername());
    }
    private Link getGameLink(HighScoreModel highScoreModel){
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getGameById(highScoreModel.getGamesModel().getId())).withRel("Game: " + highScoreModel.getGamesModel().getTitle());
    }

    private EntityModel<HighScoreModel> getHighScoreEntityModel(HighScoreModel highScoreModel){
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HighScoreApiController.class).getScoreById(highScoreModel.getScoreId())).withSelfRel();
        return EntityModel.of(highScoreModel, getUserLink(highScoreModel), getGameLink(highScoreModel), selfLink);
    }
}
