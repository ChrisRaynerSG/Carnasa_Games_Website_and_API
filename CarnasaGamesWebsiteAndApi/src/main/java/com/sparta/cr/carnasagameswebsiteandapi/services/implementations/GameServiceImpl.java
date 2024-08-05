package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.GameRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.GameServicable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class GameServiceImpl implements GameServicable {

    private final UserServiceImpl userServiceImpl;
    private final GameRepository gameRepository;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, UserServiceImpl userServiceImpl) {
        this.gameRepository = gameRepository;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public GameModel createGame(GameModel game) {
        return null;
    }

    @Override
    public GameModel updateGame(GameModel game) {
        return null;
    }

    @Override
    public GameModel deleteGame(Long gameId) {
        return null;
    }

    @Override
    public GameModel getGame(Long gameId) {
        return null;
    }

    @Override
    public List<GameModel> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public List<GameModel> getGamesByGenre(String genre) {
        return getAllGames().stream().filter(game -> game.getGenre().equalsIgnoreCase(genre)).toList();
    }

    @Override
    public List<GameModel> getGamesByTitle(String title) {
        return getAllGames().stream().filter(game -> game.getTitle().toLowerCase().contains(title.toLowerCase())).toList();
    }

    @Override
    public List<GameModel> getGamesByTitleAndGenre(String title, String genre) {
        return getGamesByGenre(genre).stream().filter(game -> game.getTitle().toLowerCase().contains(title.toLowerCase())).toList();
    }

    @Override
    @Transactional
    public List<GameModel> getGamesByCreatorId(Long creatorId) {
        if(userServiceImpl.getUser(creatorId).isEmpty()){
            //user id not found exception
            return new ArrayList<>();
        }
        return getAllGames().stream().filter(gameModel -> gameModel.getCreator().getId() == creatorId).toList();
    }

    @Override
    @Transactional
    public List<GameModel> getGamesByCreatorUsername(String creatorName){
        return getAllGames().stream().filter(gameModel -> gameModel.getCreator().getUsername().toLowerCase().contains(creatorName.toLowerCase())).toList();
    }

    @Override
    public List<GameModel> getTopTenGames() {
        List<GameModel> games = getAllGames();
        games.sort(Comparator.comparingInt(GameModel::getTimesPlayed).reversed());
        return games.subList(0, Math.min(games.size(), 10));
    }

    @Override
    public List<GameModel> getTopTenGamesByGenre(String genre) {
        List<GameModel> gamesBeforeSort = getGamesByGenre(genre);
        List<GameModel> gamesAfterSort = new ArrayList<>();
        gamesAfterSort = gamesBeforeSort;
        gamesAfterSort.sort(Comparator.comparingInt(GameModel::getTimesPlayed).reversed());
        return gamesAfterSort.subList(0, Math.min(gamesAfterSort.size(), 10));
    }
}
