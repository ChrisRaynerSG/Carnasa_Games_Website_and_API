package com.sparta.cr.carnasagameswebsiteandapi.services.interfaces;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;

import java.util.List;

public interface GameServicable {

    GameModel createGame(GameModel game);
    GameModel updateGame(GameModel game);
    GameModel deleteGame(Long gameId);

    GameModel getGame(Long gameId);
    List<GameModel> getAllGames();
    List<GameModel> getGamesByGenre(String genre);
    List<GameModel> getGamesByTitle(String title);
    List<GameModel> getGamesByTitleAndGenre(String title, String genre);
    List<GameModel> getGamesByCreator(String creator);
    List<GameModel> getTopTenGames();
    List<GameModel> getTopTenGamesByGenre(String genre);


}
