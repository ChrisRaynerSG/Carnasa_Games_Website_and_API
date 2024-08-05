package com.sparta.cr.carnasagameswebsiteandapi.repositories;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<GameModel, Long> {
}
