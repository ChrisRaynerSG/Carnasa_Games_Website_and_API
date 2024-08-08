package com.sparta.cr.carnasagameswebsiteandapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class FavouriteGameModelId {
    private static final long serialVersionUID = 1L;
    @NotNull
    @Column(name = "game_id")
    private long gameId;

    @NotNull
    @Column(name = "user_id")
    private long userId;

    @NotNull
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(@NotNull Long gameId) {
        this.gameId = gameId;
    }

    @NotNull
    public Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }
}
