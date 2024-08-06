package com.sparta.cr.carnasagameswebsiteandapi.services.interfaces;

import com.sparta.cr.carnasagameswebsiteandapi.models.CommentModel;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CommentServiceable {

    CommentModel createComment(CommentModel comment);
    CommentModel updateComment(CommentModel comment);
    CommentModel deleteComment(Long commentId);
    Optional<CommentModel> getComment(Long commentId);

    List<CommentModel> getAllComments();
    List<CommentModel> getCommentsByUser(Long userId);
    List<CommentModel> getCommentsByGame(Long gameId);
    List<CommentModel> getCommentsByGameAndUser(Long gameId, Long userId);
    List<CommentModel> getCommentsByDate(LocalDate startDate, LocalDate endDate);
    List<CommentModel> getCommentsFromToday(LocalDate today);

}
