package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.HighScoreRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.HighScoreServiceable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HighScoreServiceImpl implements HighScoreServiceable{

    private HighScoreRepository highScoreRepository;

    @Autowired
    public HighScoreServiceImpl(HighScoreRepository highScoreRepository) {
        this.highScoreRepository = highScoreRepository;
    }

    @Override
    public HighScoreModel createHighScore(UserModel user, GameModel game) {
        return null;
    }

    @Override
    public HighScoreModel updateHighScore(UserModel user, GameModel game) {
        return null;
    }

    @Override
    public HighScoreModel deleteHighScore(Long scoreId) {
        return null;
    }

    @Override
    public HighScoreModel getHighScore(Long scoreId) {
        return null;
    }

    @Override
    public List<HighScoreModel> getAllHighScores() {
        return List.of();
    }

    @Override
    public List<HighScoreModel> getHighScoresByUser(UserModel user) {
        return List.of();
    }

    @Override
    public List<HighScoreModel> getHighScoresByGame(GameModel game) {
        return List.of();
    }

    @Override
    public List<HighScoreModel> getHighScoresByGameAndUser(GameModel game, UserModel user) {
        return List.of();
    }

    @Override
    public List<HighScoreModel> getTop10HighScoresByGame(GameModel game) {
        return List.of();
    }

    @Override
    public List<HighScoreModel> getTop10HighScoresByUser(UserModel user) {
        return List.of();
    }
}
