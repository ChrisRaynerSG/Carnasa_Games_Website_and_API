package com.sparta.cr.carnasagameswebsiteandapi.models.dtos;

import java.util.Objects;

public class UserDto {
    private final long id;
    private final String username;
    private final String email;
    private final String profileImage;
    private final String description;
    private final String roles;
    private final boolean isPrivate;

    public UserDto(Long id, String username, String email, String profileImage, String description, String roles, boolean isPrivate){

        this.id = id;
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.description = description;
        this.roles = roles;
        this.isPrivate = isPrivate;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getDescription() {
        return description;
    }

    public String getRoles() {
        return roles;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return id == userDto.id && Objects.equals(username, userDto.username) && Objects.equals(email, userDto.email) && Objects.equals(profileImage, userDto.profileImage) && Objects.equals(description, userDto.description) && Objects.equals(roles, userDto.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, profileImage, description, roles);
    }
}
