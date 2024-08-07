package com.sparta.cr.carnasagameswebsiteandapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(schema = "games_website", name = "high_scores")
public class HighScoreModel {

    @Id
    @GeneratedValue
    @Column(name = "score_id")
    private long scoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonBackReference(value = "score-game")
    private GameModel gameModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "score-user")
    private UserModel userModel;

    @Column(name = "score")
    private Long score;

    @Column(name = "date")
    private LocalDate date;

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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
