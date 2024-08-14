package com.sparta.cr.carnasagameswebsiteandapi.models.dtos;

import java.util.Objects;

public class UpdatePasswordDto {

    private final String oldPassword;
    private final String newPassword;
    private final String confirmPassword;
    private final Long id;

    public UpdatePasswordDto(String oldPassword, String newPassword, String confirmPassword, Long id) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
        this.id = id;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdatePasswordDto that = (UpdatePasswordDto) o;
        return Objects.equals(oldPassword, that.oldPassword) && Objects.equals(newPassword, that.newPassword) && Objects.equals(confirmPassword, that.confirmPassword) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldPassword, newPassword, confirmPassword, id);
    }
}
