package com.sparta.cr.carnasagameswebsiteandapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FollowerModelId implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name="user_id")
    private long user_id;

    @NotNull
    @Column(name = "follower_id")
    private long follower_id;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(@NotNull long user_id) {
        this.user_id = user_id;
    }

    @NotNull
    public long getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(@NotNull long follower_id) {
        this.follower_id = follower_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FollowerModelId that = (FollowerModelId) o;

        if(user_id != that.user_id) return false;
        return follower_id == that.follower_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, follower_id);
    }
}
