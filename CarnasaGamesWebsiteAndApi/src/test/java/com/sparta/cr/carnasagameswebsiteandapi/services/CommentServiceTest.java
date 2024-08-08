package com.sparta.cr.carnasagameswebsiteandapi.services;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.commentexceptions.CommentMustHaveTextException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.*;
import com.sparta.cr.carnasagameswebsiteandapi.models.CommentModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.CommentRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.CommentServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.GameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private GameServiceImpl gameService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private List<CommentModel> comments;
    private CommentModel comment1;
    private CommentModel comment2;
    private CommentModel comment3;
    private UserModel user1;
    private UserModel user2;
    private GameModel game1;
    private GameModel game2;

    @BeforeEach
    void setUp() {
        comments = new ArrayList<>();
        comment1 = new CommentModel();
        comment2 = new CommentModel();
        comment3 = new CommentModel();

        game1 = new GameModel();
        game1.setId(1L);
        game2 = new GameModel();
        game2.setId(2L);
        user1 = new UserModel();
        user1.setId(1L);
        user2 = new UserModel();
        user2.setId(2L);

        comment1.setGamesModel(game1);
        comment1.setUserModel(user1);
        comment2.setGamesModel(game1);
        comment2.setUserModel(user1);
        comment3.setGamesModel(game2);
        comment3.setUserModel(user1);

        comment1.setDate(LocalDate.of(2021,10,25));
        comment2.setDate(LocalDate.of(2021,10,26));
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
        int actual = commentService.getCommentsByDate("2020-12-25","2021-12-25").size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void testGetCommentsByDateThrowsExceptions(){
        assertThrows(InvalidDateException.class, () -> commentService.getCommentsByDate("2220-12-25","2321-12-25"));
        assertThrows(InvalidDateException.class, () -> commentService.getCommentsByDate("2000-12-25","1985-12-25"));
        assertThrows(InvalidDateException.class, () -> commentService.getCommentsByDate("2000-1-1","2015-11-10"));
        assertThrows(InvalidDateException.class, () -> commentService.getCommentsByDate("2020-02-30","2021-12-25"));
    }
    @Test
    public void testGetCommentsByDateTodayReturnsComments(){
        int expected = 1;
        when(commentRepository.findAll()).thenReturn(comments);
        int actual = commentService.getCommentsFromToday(LocalDate.now()).size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void createCommentThrowsExceptionWhenCommentAlreadyExists(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        assertThrows(ModelAlreadyExistsException.class, () -> commentService.createComment(comment));
    }
    @Test
    void createCommentThrowsExceptionWhenUserNotFound(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        comment.setUserModel(user1);
        comment.setGamesModel(game1);
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        when(userService.getUser(1L)).thenReturn(Optional.empty());
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        when(commentRepository.save(comment)).thenReturn(comment);
        assertThrows(InvalidUserException.class, () -> commentService.createComment(comment));
    }
    @Test
    void createCommentThrowsExceptionWhenGameNotFound(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        comment.setUserModel(user1);
        comment.setGamesModel(game1);
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.empty());
        when(commentRepository.save(comment)).thenReturn(comment);
        assertThrows(InvalidGameException.class, () -> commentService.createComment(comment));
    }
    @Test
    void createCommentThrowsExceptionWhenNoText(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        comment.setUserModel(user1);
        comment.setGamesModel(game1);
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        when(commentRepository.save(comment)).thenReturn(comment);
        assertThrows(CommentMustHaveTextException.class, () -> commentService.createComment(comment));
    }
    @Test
    void createCommentReturnsNewComment(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        comment.setUserModel(user1);
        comment.setGamesModel(game1);
        comment.setCommentText("New comment");
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        when(commentRepository.save(comment)).thenReturn(comment);
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        CommentModel actual = commentService.createComment(comment);
        Assertions.assertNotNull(actual);
    }
    @Test
    void updateCommentThrowsExceptionIfCommentDoesNotExist(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        when(commentRepository.save(comment)).thenReturn(comment);
        assertThrows(ModelNotFoundException.class, () -> commentService.updateComment(comment));
    }
    @Test
    void updateCommentThrowsExceptionIfNoText(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        comment.setUserModel(user1);
        comment.setDate(LocalDate.now());
        comment.setGamesModel(game1);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        assertThrows(CommentMustHaveTextException.class, () -> commentService.updateComment(comment));
    }
    @Test
    void updateCommentThrowsExceptionIfUserDifferent(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        comment.setUserModel(user2);
        comment.setDate(LocalDate.now());
        comment.setGamesModel(game1);
        comment.setCommentText("New comment");
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment1));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(userService.getUser(2L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        assertThrows(InvalidUserException.class, () -> commentService.updateComment(comment));
    }
    @Test
    void updateCommentThrowsExceptionIfGameDifferent(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        comment.setUserModel(user1);
        comment.setDate(LocalDate.now());
        comment.setGamesModel(game2);
        comment.setCommentText("New comment");
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment1));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(2L)).thenReturn(Optional.of(game1));
        assertThrows(InvalidGameException.class, () -> commentService.updateComment(comment));
    }
    @Test
    void updateCommentReturnsUpdatedComment(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        comment.setUserModel(user1);
        comment.setDate(LocalDate.now());
        comment.setGamesModel(game1);
        comment.setCommentText("example text");
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        CommentModel actual = commentService.updateComment(comment);
        Assertions.assertNotNull(actual);
    }
    @Test
    void deleteCommentReturnsNullIfCommentDoesNotExist(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        CommentModel actual = commentService.deleteComment(1L);
        verify(commentRepository, times(0)).delete(comment);
        Assertions.assertNull(actual);
    }
    @Test
    void deleteCommentReturnsDeletedComment(){
        CommentModel comment = new CommentModel();
        comment.setId(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        CommentModel actual = commentService.deleteComment(1L);
        verify(commentRepository, times(1)).delete(comment);
        Assertions.assertNotNull(actual);
    }

    @Test
    void censorCommentReturnsOriginalCommentIfNoBadText(){
        CommentModel comment = new CommentModel();
        String expected ="This comment is fine";
        comment.setCommentText(expected);
        commentService.censorBadText(comment);
        Assertions.assertEquals(expected, comment.getCommentText());
    }

    @Test
    void censorCommentReturnsCensoredComment(){
        CommentModel comment = new CommentModel();
        String expected ="This comment is ****";
        comment.setCommentText("This comment is fuck");
        commentService.censorBadText(comment);
        Assertions.assertEquals(expected, comment.getCommentText());
    }
}
