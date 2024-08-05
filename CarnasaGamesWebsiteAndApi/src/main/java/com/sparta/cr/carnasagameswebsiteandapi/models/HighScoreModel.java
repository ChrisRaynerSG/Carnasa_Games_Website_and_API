package com.sparta.cr.carnasagameswebsiteandapi.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity(name="scores")
public class HighScoreModel {

    @Id
    @GeneratedValue
    @Column(name = "score_id")
    private long scoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private GamesModel gamesModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel userModel;

    @Column(name = "score")
    private long score;

    @Column(name = "date")
    private Date date;

    public long getScoreId() {
        return scoreId;
    }

    public void setScoreId(long scoreId) {
        this.scoreId = scoreId;
    }

    public GamesModel getGamesModel() {
        return gamesModel;
    }

    public void setGamesModel(GamesModel gamesModel) {
        this.gamesModel = gamesModel;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
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
                "gamesModel=" + gamesModel.getTitle() +
                ", userModel=" + userModel.getUsername() +
                ", score=" + score +
                ", date=" + date +
                '}';
    }
}
