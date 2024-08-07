package com.sparta.cr.carnasagameswebsiteandapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(schema = "games_website", name = "followers")
public class FollowerModel {

    @EmbeddedId
    private FollowerModelId id;

    @MapsId("user_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "follower-user")
    private UserModel user;

    @MapsId("follower_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    @JsonBackReference(value = "follower-follower")
    private UserModel follower;

    public FollowerModelId getId() {
        return id;
    }

    public void setId(FollowerModelId id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public UserModel getFollower() {
        return follower;
    }

    public void setFollower(UserModel follower) {
        this.follower = follower;
    }
}
