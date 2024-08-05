package com.sparta.cr.carnasagameswebsiteandapi.services.interfaces;

import com.sparta.cr.carnasagameswebsiteandapi.models.CommentModel;

import java.util.Date;
import java.util.List;

public interface CommentServiceable {

    CommentModel createComment(CommentModel comment);
    CommentModel updateComment(CommentModel comment);
    CommentModel deleteComment(Long commentId);
    CommentModel getComment(Long commentId);

    List<CommentModel> getAllComments();
    List<CommentModel> getCommentsByUser(Long userId);
    List<CommentModel> getCommentsByGame(Long gameId);
    List<CommentModel> getCommentsByGameAndUser(Long gameId, Long userId);
    List<CommentModel> getCommentsByDate(Date startDate, Date endDate);
    List<CommentModel> getCommentsFromToday(Date today);

}
