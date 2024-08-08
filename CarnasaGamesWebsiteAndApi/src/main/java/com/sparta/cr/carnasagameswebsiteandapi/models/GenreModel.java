package com.sparta.cr.carnasagameswebsiteandapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "games_website", name = "game_genres")
public class GenreModel {

    @Id
    @Column(name = "genre")
    private String genre;

    @Column(name="default_image")
    private String genreDefaultImage;

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getGenreDefaultImage() {
        return genreDefaultImage;
    }

    public void setGenreDefaultImage(String genreDefaultImage) {
        this.genreDefaultImage = genreDefaultImage;
    }
}
