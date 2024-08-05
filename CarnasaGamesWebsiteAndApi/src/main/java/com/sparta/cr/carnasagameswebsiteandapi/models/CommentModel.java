package com.sparta.cr.carnasagameswebsiteandapi.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity(name = "comments")
public class CommentModel {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private GamesModel gamesModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel userModel;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "date")
    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "CommentModel{" +
                "gamesModel=" + gamesModel.getTitle() +
                ", userModel=" + userModel.getUsername() +
                ", commentText='" + commentText + '\'' +
                ", date=" + date +
                '}';
    }
}
