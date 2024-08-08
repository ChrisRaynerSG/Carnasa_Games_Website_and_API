package com.sparta.cr.carnasagameswebsiteandapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(schema = "games_website", name = "favourite_games")
public class FavouriteGameModel {

    @EmbeddedId
    private FavouriteGameModelId favouriteGameModelId;

    @MapsId("gameId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonBackReference(value = "game-favourite")
    private GameModel gameModel;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-favourite")
    private UserModel userModel;

    @Column(name = "number_visits")
    @ColumnDefault("0")
    private int numberOfVisits;

    @Column(name = "is_favourite")
    private boolean isFavourite;

    public FavouriteGameModelId getFavouriteGameModelId() {
        return favouriteGameModelId;
    }

    public void setFavouriteGameModelId(FavouriteGameModelId favouriteGameModelId) {
        this.favouriteGameModelId = favouriteGameModelId;
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public int getNumberOfVisits() {
        return numberOfVisits;
    }

    public void setNumberOfVisits(int numberOfVisits) {
        this.numberOfVisits = numberOfVisits;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Override
    public String toString() {
        return "FavouriteGameModel{" +
                ", gameModel=" + gameModel.getTitle() +
                ", userModel=" + userModel.getUsername() +
                ", numberOfVisits=" + numberOfVisits +
                ", isFavourite=" + isFavourite +
                '}';
    }
}
