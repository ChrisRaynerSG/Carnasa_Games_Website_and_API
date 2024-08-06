package com.sparta.cr.carnasagameswebsiteandapi;

import com.sparta.cr.carnasagameswebsiteandapi.models.CommentModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.CommentRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.CommentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private List<CommentModel> comments;
    private CommentModel comment1;
    private CommentModel comment2;
    private CommentModel comment3;

    @BeforeEach
    void setUp() {
        comments = new ArrayList<>();
        comment1 = new CommentModel();
        comment2 = new CommentModel();
        comment3 = new CommentModel();

        GameModel game1 = new GameModel();
        game1.setId(1L);
        GameModel game2 = new GameModel();
        game2.setId(2L);
        UserModel user1 = new UserModel();
        user1.setId(1L);

        comment1.setGamesModel(game1);
        comment1.setUserModel(user1);
        comment2.setGamesModel(game1);
        comment2.setUserModel(user1);
        comment3.setGamesModel(game2);
        comment3.setUserModel(user1);

        comment1.setDate(LocalDate.of(2021,12,25));
        comment2.setDate(LocalDate.of(2021,12,26));
        comment3.setDate(LocalDate.now());

        comments.add(comment1);
        comments.add(comment2);
        comments.add(comment3);
    }

    @Test
    public void testEmptyOptionalReturnedWhenCommentDoesNotExist() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<CommentModel> comment = commentService.getComment(1L);
        Assertions.assertTrue(comment.isEmpty());
    }

    @Test
    public void testCommentReturnedWhenCommentExists() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(new CommentModel()));
        Optional<CommentModel> comment = commentService.getComment(1L);
        Assertions.assertTrue(comment.isPresent());
    }
    @Test
    public void testGetAllCommentsReturnsComments(){
        int expected = 3;
        when(commentRepository.findAll()).thenReturn(comments);
        int actual = commentService.getAllComments().size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void testGetCommentsByGameReturnsComments(){
        int expected = 2;
        when(commentRepository.findAll()).thenReturn(comments);
        int actual = commentService.getCommentsByGame(1L).size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void testGetCommentsByUserReturnsComments(){
        int expected = 3;
        when(commentRepository.findAll()).thenReturn(comments);
        int actual = commentService.getCommentsByUser(1L).size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void testGetCommentsByUserAndGameReturnsComments(){
        int expected = 1;
        when(commentRepository.findAll()).thenReturn(comments);
        int actual = commentService.getCommentsByGameAndUser(2L,1L).size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void testGetCommentsByDateRangeReturnsComments(){
        int expected = 2;
        when(commentRepository.findAll()).thenReturn(comments);
        int actual = commentService.getCommentsByDate(LocalDate.of(2020,12,12),LocalDate.of(2022,12,12)).size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void testGetCommentsByDateTodayReturnsComments(){
        int expected = 1;
        when(commentRepository.findAll()).thenReturn(comments);
        int actual = commentService.getCommentsFromToday(LocalDate.now()).size();
        Assertions.assertEquals(expected, actual);
    }
}
