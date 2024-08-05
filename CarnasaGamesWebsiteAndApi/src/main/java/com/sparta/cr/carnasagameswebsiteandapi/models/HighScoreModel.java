package com.sparta.cr.carnasagameswebsiteandapi.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity(name="scores")
public class HighScoreModel {

    @Id
    @GeneratedValue
    @Column(name = "score_id")
    private Long scoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private GameModel gameModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel userModel;

    @Column(name = "score")
    private Long score;

    @Column(name = "date")
    private Date date;

    public Long getScoreId() {
        return scoreId;
    }

    public void setScoreId(Long scoreId) {
        this.scoreId = scoreId;
    }

    public GameModel getGamesModel() {
        return gameModel;
    }

    public void setGamesModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "HighScoreModel{" +
                "gamesModel=" + gameModel.getTitle() +
                ", userModel=" + userModel.getUsername() +
                ", score=" + score +
                ", date=" + date +
                '}';
    }
}
