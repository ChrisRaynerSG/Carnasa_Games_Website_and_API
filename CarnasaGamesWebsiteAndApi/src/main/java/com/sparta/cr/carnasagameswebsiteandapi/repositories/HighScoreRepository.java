package com.sparta.cr.carnasagameswebsiteandapi.repositories;

import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HighScoreRepository extends JpaRepository<HighScoreModel, Long> {
}
