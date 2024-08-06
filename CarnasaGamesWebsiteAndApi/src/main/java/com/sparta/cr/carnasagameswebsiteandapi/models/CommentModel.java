package com.sparta.cr.carnasagameswebsiteandapi.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(schema = "games_website", name = "comments")
public class CommentModel {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private GameModel gameModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel userModel;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "is_updated")
    private Boolean isUpdated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getIsUpdated() {
        return isUpdated;
    }
    public void setIsUpdated(Boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    @Override
    public String toString() {
        return "CommentModel{" +
                "gamesModel=" + gameModel.getTitle() +
                ", userModel=" + userModel.getUsername() +
                ", commentText='" + commentText + '\'' +
                ", date=" + date +
                '}';
    }
}
