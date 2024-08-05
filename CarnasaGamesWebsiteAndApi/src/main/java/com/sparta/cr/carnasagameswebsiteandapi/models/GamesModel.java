package com.sparta.cr.carnasagameswebsiteandapi.models;

import jakarta.persistence.*;

@Entity(name = "games")
public class GamesModel {

    @Id
    @GeneratedValue
    @Column(name = "game_id", nullable = false, updatable = false)
    private long id;

    @Column(name = "title")
    private String title;

    @JoinColumn(name = "creator", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserModel creator;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    @Column(name = "genre", nullable = false)
    private String genre;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "GamesModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creator=" + creator +
                ", isPublished=" + isPublished +
                ", genre='" + genre + '\'' +
                '}';
    }
}
