package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidGameException;
import com.sparta.cr.carnasagameswebsiteandapi.models.FavouriteGameModel;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.FavouriteGameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.GameServiceImpl;
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
public class FavouriteGameApiController {

    private final FavouriteGameServiceImpl favouriteGameService;
    private final GameServiceImpl gameService;
    private final UserServiceImpl userService;

    @Autowired
    public FavouriteGameApiController(FavouriteGameServiceImpl favouriteGameService, GameServiceImpl gameService, UserServiceImpl userService) {
        this.favouriteGameService = favouriteGameService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/search/id/{userId}/top-10-games")
    public ResponseEntity<CollectionModel<EntityModel<FavouriteGameModel>>> getTopTenGamesPlayed(@PathVariable Long userId){
        List<EntityModel<FavouriteGameModel>> favouriteGameModels = favouriteGameService
                .getTopTenFavouriteGamesByUserId(userId)
                .stream()
                .map( this::getFavouriteGameEntityModel)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(favouriteGameModels));
    }

    @GetMapping("/search/id/{userId}/favourite-games")
    public ResponseEntity<CollectionModel<EntityModel<FavouriteGameModel>>> getFavouriteGames(@PathVariable Long userId){
        List<EntityModel<FavouriteGameModel>> favouriteGameModels = favouriteGameService
                .getAllFavouriteGamesByUserId(userId)
                .stream()
                .map( this::getFavouriteGameEntityModel)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(favouriteGameModels));
    }

    @PostMapping("/new/favourite-game")
    public ResponseEntity<EntityModel<FavouriteGameModel>> createFavouriteGame(@RequestBody FavouriteGameModel model){
        favouriteGameService.validateNewFavouriteGame(model);
        favouriteGameService.createFavouriteGame(model);
        return new ResponseEntity<>(getFavouriteGameEntityModel(model), HttpStatus.CREATED);
    }

    @PutMapping("/update/favourite-game/{userId}/{gameId}")
    public ResponseEntity<EntityModel<FavouriteGameModel>> updateFavouriteGame(@PathVariable Long userId, @PathVariable Long gameId, @RequestBody FavouriteGameModel model){
        if(!model.getFavouriteGameModelId().getGameId().equals(gameId)){
            throw new InvalidGameException("Cannot update relationship as url gameId does not match gameId");
        }
        if(!model.getFavouriteGameModelId().getUserId().equals(userId)){
            throw new InvalidGameException("Cannot update relationship as url userId does not match userId");
        }
        favouriteGameService.validateExistingFavouriteGame(model);
        favouriteGameService.updateFavouriteGame(model);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/favourite-game/{userId}/{gameId}")
    public ResponseEntity<FavouriteGameModel> deleteFavouriteGame(@PathVariable Long userId, @PathVariable Long gameId){
        if(userService.getUser(userId).isEmpty()||gameService.getGame(gameId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        else{
            favouriteGameService.deleteFavouriteGame(userId,gameId);
            return ResponseEntity.noContent().build();
        }
    }

    public Link getGameLink(FavouriteGameModel model){
        return gameService.getGame(model.getGameModel().getId()).map(game ->
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(GameApiController.class).getGameById(game.getId()))
                        .withRel("Game: " + game.getTitle())).get();
    }

    private EntityModel<FavouriteGameModel> getFavouriteGameEntityModel(FavouriteGameModel model){
        return EntityModel.of(model, getGameLink(model));
    }
}
