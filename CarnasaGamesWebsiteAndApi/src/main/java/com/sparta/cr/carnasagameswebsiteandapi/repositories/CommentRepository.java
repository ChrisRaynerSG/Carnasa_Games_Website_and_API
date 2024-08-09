package com.sparta.cr.carnasagameswebsiteandapi.repositories;

import com.sparta.cr.carnasagameswebsiteandapi.models.CommentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel, Long> {
    Page<CommentModel> findAllByUserModel_UsernameContainingIgnoreCase(String username, Pageable pageable);
}
