package com.sparta.cr.carnasagameswebsiteandapi.services.interfaces;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HighScoreServiceable {

    HighScoreModel createHighScore(UserModel user, GameModel game);
    HighScoreModel updateHighScore(UserModel user, GameModel game);
    HighScoreModel deleteHighScore(Long scoreId);
    Optional<HighScoreModel> getHighScore(Long scoreId);

    List<HighScoreModel> getAllHighScores();
    List<HighScoreModel> getHighScoresByUser(Long userId);
    List<HighScoreModel> getHighScoresByGame(Long gameId);
    List<HighScoreModel> getHighScoresByGameAndUser(Long userId, Long gameId);
    List<HighScoreModel> getTop10HighScoresByGame(Long gameId);
    List<HighScoreModel> getTop10HighScoresByUser(Long userId);
    List<HighScoreModel> getTop10HighScoresToday(Long gameId, LocalDate today);


}
