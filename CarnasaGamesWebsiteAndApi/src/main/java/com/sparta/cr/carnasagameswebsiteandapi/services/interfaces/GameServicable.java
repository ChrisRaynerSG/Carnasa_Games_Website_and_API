package com.sparta.cr.carnasagameswebsiteandapi.services.interfaces;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface GameServicable {

    GameModel createGame(GameModel game);
    GameModel updateGame(GameModel game);
    GameModel deleteGame(Long gameId);

    Optional<GameModel> getGame(Long gameId);
    Page<GameModel> getAllGames(int page, int size);
    Page<GameModel> getGamesByGenre(String genre, int page, int size);
    Page<GameModel> getGamesByTitle(String title, int page, int size);
    Page<GameModel> getGamesByTitleAndGenre(String title, String genre, int page, int size);
    Page<GameModel> getGamesByCreatorId(Long creatorId, int page, int size);
    Page<GameModel> getGamesByCreatorUsername(String name, int page, int size);
    Page<GameModel> getTopTenGames();
    Page<GameModel> getTopTenGamesByGenre(String genre);


}
