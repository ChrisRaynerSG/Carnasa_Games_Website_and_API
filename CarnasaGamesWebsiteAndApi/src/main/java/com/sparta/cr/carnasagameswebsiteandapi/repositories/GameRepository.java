package com.sparta.cr.carnasagameswebsiteandapi.repositories;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<GameModel, Long> {
    Page<GameModel> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<GameModel> findAllByGenreContainingIgnoreCase(String genre, Pageable pageable);
    Page<GameModel> findByCreator_UsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<GameModel> findByCreator_Id(Long id, Pageable pageable);
}
