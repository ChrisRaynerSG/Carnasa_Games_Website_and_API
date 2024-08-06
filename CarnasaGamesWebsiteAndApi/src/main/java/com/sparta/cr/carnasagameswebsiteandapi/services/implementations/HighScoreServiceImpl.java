package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.HighScoreRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.HighScoreServiceable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class HighScoreServiceImpl implements HighScoreServiceable{

    private HighScoreRepository highScoreRepository;

    @Autowired
    public HighScoreServiceImpl(HighScoreRepository highScoreRepository) {
        this.highScoreRepository = highScoreRepository;
    }

    @Override
    public HighScoreModel createHighScore(HighScoreModel highScoreModel) {
        if(getHighScore(highScoreModel.getScoreId()).isPresent()){
            return null;
        }
        highScoreModel.setDate(LocalDate.now());
        return highScoreRepository.save(highScoreModel);
    }

    @Override
    public HighScoreModel updateHighScore(HighScoreModel highScoreModel) {
        if(getHighScore(highScoreModel.getScoreId()).isEmpty()){
            return null;
        }
        return highScoreRepository.save(highScoreModel);
    }

    @Override
    public HighScoreModel deleteHighScore(Long scoreId) {
        if(getHighScore(scoreId).isPresent()){
            HighScoreModel highScoreModel = getHighScore(scoreId).get();
            highScoreRepository.delete(highScoreModel);
            return highScoreModel;
        }
        return null;
    }

    @Override
    public Optional<HighScoreModel> getHighScore(Long scoreId) {
        return highScoreRepository.findById(scoreId);
    }

    @Override
    public List<HighScoreModel> getAllHighScores() {
        return highScoreRepository.findAll();
    }

    @Override
    public List<HighScoreModel> getHighScoresByUser(Long userId) {
        return highScoreRepository.findAll()
                .stream()
                .filter(highScoreModel -> highScoreModel.getUserModel().getId().equals(userId))
                .toList();
    }

    @Override
    public List<HighScoreModel> getHighScoresByGame(Long gameId) {
        return highScoreRepository.findAll()
                .stream()
                .filter(highScoreModel -> highScoreModel.getGamesModel().getId().equals(gameId))
                .toList();
    }

    @Override
    public List<HighScoreModel> getHighScoresByGameAndUser(Long userId, Long gameId) {
        return getHighScoresByGame(gameId)
                .stream()
                .filter(highScoreModel -> highScoreModel
                        .getUserModel()
                        .getId()
                        .equals(userId))
                .toList();
    }

    @Override
    public List<HighScoreModel> getTop10HighScoresByGame(Long gameId) {
        List<HighScoreModel> highScores = new ArrayList<>(getHighScoresByGame(gameId));
        highScores.sort(Comparator.comparingLong(HighScoreModel::getScore).reversed());
        return highScores.subList(0, Math.min(highScores.size(), 10));
    }

    @Override
    public List<HighScoreModel> getTop10HighScoresByUser(Long userId) {
        List<HighScoreModel> highScores = new ArrayList<>(getHighScoresByUser(userId));
        highScores.sort(Comparator.comparingLong(HighScoreModel::getScore).reversed());
        return highScores.subList(0, Math.min(highScores.size(), 10));
    }

    @Override
    public List<HighScoreModel> getTop10HighScoresToday(Long gameId, LocalDate today) {
        List<HighScoreModel> highScores = new ArrayList<>(getHighScoresToday(gameId, today));
        highScores.sort(Comparator.comparingLong(HighScoreModel::getScore).reversed());
        return highScores.subList(0, Math.min(highScores.size(), 10));
    }
    @Override
    public List<HighScoreModel> getHighScoresToday(Long gameId, LocalDate today) {
        return getHighScoresByGame(gameId)
                .stream()
                .filter(highScoreModel -> highScoreModel
                        .getDate()
                        .equals(today))
                .toList();
    }
}
