package com.sparta.cr.carnasagameswebsiteandapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(schema = "games_website", name = "games")
public class GameModel {

    @Id
    @GeneratedValue
    @Column(name = "game_id", nullable = false, updatable = false)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @JoinColumn(name = "creator", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference(value = "game-creator")
    private UserModel creator;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished;

    @Column(name = "genre", nullable = false)
    private String genre;

    @Column(name = "times_played")
    private Integer timesPlayed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserModel getCreator() {
        return creator;
    }

    public void setCreator(UserModel creator) {
        this.creator = creator;
    }

    public Boolean isPublished() {
        return isPublished;
    }

    public void setPublished(Boolean published) {
        isPublished = published;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(Integer timesPlayed) {
        this.timesPlayed = timesPlayed;
    }

    @Override
    public String toString() {
        return "GamesModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creator=" + creator +
                ", isPublished=" + isPublished +
                ", genre='" + genre + '\'' +
                ", timesPlayed=" + timesPlayed +
                '}';
    }
}
