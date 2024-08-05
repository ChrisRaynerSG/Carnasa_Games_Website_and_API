package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.GameRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.GameServicable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameServicable {

    private GameRepository gameRepository;
    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
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
        return List.of();
    }

    @Override
    public List<GameModel> getGamesByTitle(String title) {
        return List.of();
    }

    @Override
    public List<GameModel> getGamesByTitleAndGenre(String title, String genre) {
        return List.of();
    }

    @Override
    public List<GameModel> getGamesByCreator(String creator) {
        return List.of();
    }

    @Override
    public List<GameModel> getTopTenGames() {
        return List.of();
    }

    @Override
    public List<GameModel> getTopTenGamesByGenre(String genre) {
        return List.of();
    }
}
