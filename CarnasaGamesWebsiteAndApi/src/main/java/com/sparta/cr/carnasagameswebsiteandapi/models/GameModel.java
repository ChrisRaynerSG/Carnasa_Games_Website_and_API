package com.sparta.cr.carnasagameswebsiteandapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "genre")
    private GenreModel genre;

    @Column(name = "times_played")
    private Integer timesPlayed;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;

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

    public GenreModel getGenre() {
        return genre;
    }

    public void setGenre(GenreModel genre) {
        this.genre = genre;
    }

    public Integer getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(Integer timesPlayed) {
        this.timesPlayed = timesPlayed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
