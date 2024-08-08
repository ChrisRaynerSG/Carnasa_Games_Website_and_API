package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.models.FavouriteGameModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/")
public class FavouriteGameController {

    @GetMapping("/search/id/{userId}/top-10-games")
    public ResponseEntity<CollectionModel<EntityModel<FavouriteGameModel>>> getTopTenGamesPlayed(@PathVariable Long userId){

    }

    @GetMapping("/search/id/{userId}/favourite-games")
    public ResponseEntity<CollectionModel<EntityModel<FavouriteGameModel>>> getFavouriteGames(@PathVariable Long userId){

    }

    @PostMapping("/new/favourite-game")
    public ResponseEntity<EntityModel<FavouriteGameModel>> createFavouriteGame(@RequestBody FavouriteGameModel model){

    }

    @PutMapping("/update/favourite-game/{userId}/{gameId}")
    public ResponseEntity<EntityModel<FavouriteGameModel>> updateFavouriteGame(@PathVariable Long userId, @PathVariable Long gameId){

    }

    @DeleteMapping("/delete/favourite-game/{userId}/{gameId}")
    public ResponseEntity<FavouriteGameModel> deleteFavouriteGame(@PathVariable Long userId, @PathVariable Long gameId){

    }
}
