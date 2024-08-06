package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.NoGameException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.NoUserException;
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

    private final HighScoreRepository highScoreRepository;
    private final UserServiceImpl userService;
    private final GameServiceImpl gameService;

    @Autowired
    public HighScoreServiceImpl(HighScoreRepository highScoreRepository, UserServiceImpl userService, GameServiceImpl gameService) {
        this.highScoreRepository = highScoreRepository;
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public HighScoreModel createHighScore(HighScoreModel highScoreModel) {
        if(validateNewHighScore(highScoreModel)) {
            highScoreModel.setUserModel(userService.getUser(highScoreModel.getUserModel().getId()).get());
            highScoreModel.setGamesModel(gameService.getGame(highScoreModel.getGamesModel().getId()).get());
            highScoreModel.setDate(LocalDate.now());
            return highScoreRepository.save(highScoreModel);
        }
        return null;
    }
    @Override
    public HighScoreModel updateHighScore(HighScoreModel highScoreModel) {
        if(validateExistingHighScore(highScoreModel)) {
            HighScoreModel beforeUpdate = getHighScore(highScoreModel.getScoreId()).get();
            highScoreModel.setDate(beforeUpdate.getDate());
            highScoreModel.setUserModel(userService.getUser(highScoreModel.getUserModel().getId()).get());
            highScoreModel.setGamesModel(gameService.getGame(highScoreModel.getGamesModel().getId()).get());
            return highScoreRepository.save(highScoreModel);
        }
        return null;
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

    public boolean validateNewHighScore(HighScoreModel highScoreModel) {
        if(getHighScore(highScoreModel.getScoreId()).isPresent()){
            return false; //HighScoreAlreadyExists
        }
        if(userService.getUser(highScoreModel.getUserModel().getId()).isEmpty()){
            throw new NoUserException("Cannot create comment as user with ID: " + highScoreModel.getUserModel().getId()+" does not exist." );
        }
        if(gameService.getGame(highScoreModel.getGamesModel().getId()).isEmpty()){
            throw new NoGameException("Cannot create comment as game with ID: " + highScoreModel.getGamesModel().getId() + " does not exist." );
        }
        if(highScoreModel.getScore()>=Long.MAX_VALUE || highScoreModel.getScore()<=Long.MIN_VALUE){
            return false; //outOfBoundsException
        }
        return true;
    }

    public boolean validateExistingHighScore(HighScoreModel highScoreModel) {
        if(getHighScore(highScoreModel.getScoreId()).isEmpty()){
            return false; //HighScoreDoesntExist
        }
        HighScoreModel beforeUpdate = getHighScore(highScoreModel.getScoreId()).get();
        if(!beforeUpdate.getUserModel().getId().equals(highScoreModel.getUserModel().getId())){
            throw new NoUserException("Cannot update HighScore as new user ID detected");
        }
        if(!beforeUpdate.getGamesModel().getId().equals(highScoreModel.getGamesModel().getId())){
            throw new NoGameException("Cannot update HighScore as new game ID detected");
        }
        if(highScoreModel.getScore()>=Long.MAX_VALUE || highScoreModel.getScore()<=Long.MIN_VALUE){
            return false; //outOfBoundsException
        }
        return true;
    }
}
