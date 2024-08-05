package com.sparta.cr.carnasagameswebsiteandapi.services.interfaces;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;

import java.util.List;

public interface HighScoreServiceable {

    HighScoreModel createHighScore(UserModel user, GameModel game);
    HighScoreModel updateHighScore(UserModel user, GameModel game);
    HighScoreModel deleteHighScore(Long scoreId);
    HighScoreModel getHighScore(Long scoreId);

    List<HighScoreModel> getAllHighScores();
    List<HighScoreModel> getHighScoresByUser(UserModel user);
    List<HighScoreModel> getHighScoresByGame(GameModel game);
    List<HighScoreModel> getHighScoresByGameAndUser(GameModel game, UserModel user);
    List<HighScoreModel> getTop10HighScoresByGame(GameModel game);
    List<HighScoreModel> getTop10HighScoresByUser(UserModel user);


}
