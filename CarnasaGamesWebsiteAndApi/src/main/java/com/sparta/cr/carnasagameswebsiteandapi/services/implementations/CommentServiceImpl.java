package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.models.CommentModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.CommentRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.CommentServiceable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentServiceable {

    private CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentModel createComment(CommentModel comment) {
        return null;
    }

    @Override
    public CommentModel updateComment(CommentModel comment) {
        return null;
    }

    @Override
    public CommentModel deleteComment(Long commentId) {
        return null;
    }

    @Override
    public Optional<CommentModel> getComment(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public List<CommentModel> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public List<CommentModel> getCommentsByUser(Long userId) {
        return getAllComments()
                .stream()
                .filter(commentModel -> commentModel.getUserModel().getId().equals(userId))
                .toList();
    }

    @Override
    public List<CommentModel> getCommentsByGame(Long gameId) {
        return getAllComments()
                .stream()
                .filter(commentModel -> commentModel.getGamesModel().getId().equals(gameId))
                .toList();
    }

    @Override
    public List<CommentModel> getCommentsByGameAndUser(Long gameId, Long userId) {
        return getCommentsByGame(gameId).stream().filter(commentModel -> commentModel.getUserModel().getId().equals(userId)).toList();
    }

    @Override
    public List<CommentModel> getCommentsByDate(Date startDate, Date endDate) {
        return List.of();
    }

    @Override
    public List<CommentModel> getCommentsFromToday(Date today) {
        return List.of();
    }
}
