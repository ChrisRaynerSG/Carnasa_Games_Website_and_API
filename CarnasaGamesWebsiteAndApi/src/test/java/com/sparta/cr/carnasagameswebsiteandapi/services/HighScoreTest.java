package com.sparta.cr.carnasagameswebsiteandapi.services;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidGameException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidUserException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelAlreadyExistsException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.HighScoreRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.GameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.HighScoreServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HighScoreTest {

    private static final Logger log = LoggerFactory.getLogger(HighScoreTest.class);

    @Mock
    HighScoreRepository highScoreRepository;
    @Mock
    UserServiceImpl userService;
    @Mock
    GameServiceImpl gameService;

    @InjectMocks
    HighScoreServiceImpl highScoreService;

    private HighScoreModel highScore1;
    private HighScoreModel highScore2;
    private HighScoreModel highScore3;
    private List<HighScoreModel> highScoreModels;
    private List<HighScoreModel> topHighScoreModels;
    private UserModel user1;
    private UserModel user2;
    private GameModel game1;
    private GameModel game2;


    @BeforeEach
    void setUp() {
        highScore1 = new HighScoreModel();
        highScore2 = new HighScoreModel();
        highScore3 = new HighScoreModel();
        highScoreModels = new ArrayList<>();
        topHighScoreModels = new ArrayList<>();

        game1 = new GameModel();
        game2 = new GameModel();
        game1.setId(1L);
        game2.setId(2L);

        user1 = new UserModel();
        user1.setId(1L);
        user2 = new UserModel();
        user2.setId(2L);

        highScore1.setGamesModel(game1);
        highScore2.setGamesModel(game2);
        highScore3.setGamesModel(game2);
        highScore1.setUserModel(user1);
        highScore2.setUserModel(user1);
        highScore3.setUserModel(user2);
        highScore1.setScore(100L);
        highScore2.setScore(200L);
        highScore3.setScore(300L);

        highScoreModels.add(highScore1);
        highScoreModels.add(highScore2);
        highScoreModels.add(highScore3);

        for (int i = 0; i <= 40; i++) {
            HighScoreModel highScoreModel = new HighScoreModel();
            if(i%2==0) {
                highScoreModel.setGamesModel(game1);
            }
            else {
                highScoreModel.setGamesModel(game2);
            }
            if(i%3==0) {
                highScoreModel.setUserModel(user1);
                highScoreModel.setDate(LocalDate.of(2000,12,25));
            }
            else {
                highScoreModel.setUserModel(user2);
                highScoreModel.setDate(LocalDate.now());
            }
            highScoreModel.setScore(i*100L);
            topHighScoreModels.add(highScoreModel);
        }
    }

    @Test
    void testGetAllHighScores() {
        int expected = 3;
        when(highScoreRepository.findAll()).thenReturn(highScoreModels);
        int actual = highScoreService.getAllHighScores().size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void testScoreReturnedWhenScoreExists() {
        when(highScoreRepository.findById(1L)).thenReturn(Optional.of(new HighScoreModel()));
        Optional<HighScoreModel> score = highScoreService.getHighScore(1L);
        Assertions.assertTrue(score.isPresent());
    }
    @Test
    public void testNoScoreReturnedWhenScoreDoesNotExist() {
        when(highScoreRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<HighScoreModel> score = highScoreService.getHighScore(1L);
        Assertions.assertFalse(score.isPresent());
    }
    @Test
    public void testGetScoresByGameReturnsScores(){
        int expected = 2;
        when(highScoreRepository.findAll()).thenReturn(highScoreModels);
        int actual = highScoreService.getHighScoresByGame(2L).size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void testGetScoresByUserReturnsScores(){
        int expected = 2;
        when(highScoreRepository.findAll()).thenReturn(highScoreModels);
        int actual = highScoreService.getHighScoresByUser(1L).size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void testGetScoresByUserAndGameReturnsScores(){
        int expected = 1;
        when(highScoreRepository.findAll()).thenReturn(highScoreModels);
        int actual = highScoreService.getHighScoresByGameAndUser(1L, 1L).size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void testGetTopHighScoresByGameReturnsScores(){
        int expected = 10;
        when(highScoreRepository.findAll()).thenReturn(topHighScoreModels);
        List<HighScoreModel> top10 = highScoreService.getTop10HighScoresByGame(1L);
        for (HighScoreModel highScore : top10) {
            log.atInfo().log("High Score : " + highScore.toString());
        }
        Assertions.assertEquals(expected, top10.size());
    }
    @Test
    public void testGetTopHighScoresByUserReturnsScores(){
        int expected = 10;
        when(highScoreRepository.findAll()).thenReturn(topHighScoreModels);
        List<HighScoreModel> top10 = highScoreService.getTop10HighScoresByUser(2L);
        for (HighScoreModel highScore : top10) {
            log.atInfo().log("High Score : " + highScore.toString());
        }
        Assertions.assertEquals(expected, top10.size());
    }
    @Test
    void testGetTop10HighScoresToday(){
        int expected = 10;
        when(highScoreRepository.findAll()).thenReturn(topHighScoreModels);
        List<HighScoreModel> top10 = highScoreService.getTop10HighScoresToday(1L, LocalDate.now());
        for (HighScoreModel highScore : top10) {
            log.atInfo().log("High Score : " + highScore.toString());
        }
        Assertions.assertEquals(expected, top10.size());
    }
    @Test
    void createScoreReturnsNullWhenScoreAlreadyExists(){
        HighScoreModel highScoreModel = new HighScoreModel();
        highScoreModel.setScoreId(1L);
        when(highScoreRepository.findById(1L)).thenReturn(Optional.of(highScoreModel));
        assertThrows(ModelAlreadyExistsException.class, () -> highScoreService.createHighScore(highScoreModel));
    }
    @Test
    void createScoreReturnsScoreWhenScoreDoesNotExist(){
        HighScoreModel highScoreModel = new HighScoreModel();
        highScoreModel.setScoreId(1L);
        highScoreModel.setUserModel(user1);
        highScoreModel.setGamesModel(game1);
        highScoreModel.setScore(100L);
        when(highScoreRepository.findById(1L)).thenReturn(Optional.empty());
        when(highScoreRepository.save(highScoreModel)).thenReturn(highScoreModel);
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        HighScoreModel actual = highScoreService.createHighScore(highScoreModel);
        Assertions.assertNotNull(actual);
    }

    @Test
    void updateHighScoreThrowsExceptionWhenScoreDoesNotExist(){
        HighScoreModel highScoreModel = new HighScoreModel();
        highScoreModel.setScoreId(1L);
        when(highScoreRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> highScoreService.updateHighScore(highScoreModel));
    }
    @Test
    void updateHighScoreThrowsExceptionWhenGameIsDifferent(){
        HighScoreModel highScoreModel = new HighScoreModel();
        highScoreModel.setScoreId(1L);
        highScoreModel.setUserModel(user1);
        highScoreModel.setGamesModel(game2);
        highScoreModel.setScore(100L);
        when(highScoreRepository.findById(1L)).thenReturn(Optional.of(highScore1));
        when(highScoreRepository.save(highScoreModel)).thenReturn(highScoreModel);
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        assertThrows(InvalidGameException.class, () -> highScoreService.updateHighScore(highScoreModel));
    }
    @Test
    void updateHighScoreThrowsExceptionWhenUserIsDifferent(){
        HighScoreModel highScoreModel = new HighScoreModel();
        highScoreModel.setScoreId(1L);
        highScoreModel.setUserModel(user2);
        highScoreModel.setGamesModel(game1);
        highScoreModel.setScore(100L);
        when(highScoreRepository.findById(1L)).thenReturn(Optional.of(highScore1));
        when(highScoreRepository.save(highScoreModel)).thenReturn(highScoreModel);
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        assertThrows(InvalidUserException.class, () -> highScoreService.updateHighScore(highScoreModel));
    }
    @Test
    void updateHighScoreReturnsScoreWhenScoreExists(){
        HighScoreModel highScoreModel = new HighScoreModel();
        highScoreModel.setScoreId(1L);
        highScoreModel.setUserModel(user1);
        highScoreModel.setGamesModel(game1);
        highScoreModel.setScore(100L);
        when(highScoreRepository.findById(1L)).thenReturn(Optional.of(highScore1));
        when(highScoreRepository.save(highScoreModel)).thenReturn(highScoreModel);
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        HighScoreModel actual = highScoreService.updateHighScore(highScoreModel);
        Assertions.assertNotNull(actual);
    }
    @Test
    void deleteHighScoreReturnsNullWhenScoreDoesNotExist(){
        when(highScoreRepository.findById(1L)).thenReturn(Optional.empty());
        HighScoreModel actual = highScoreService.deleteHighScore(1L);
        verify(highScoreRepository, times(0)).delete(any(HighScoreModel.class));
        Assertions.assertNull(actual);
    }
    @Test
    void deleteHighScoreReturnsScoreWhenScoreExists(){
        HighScoreModel highScoreModel = new HighScoreModel();
        highScoreModel.setScoreId(1L);
        when(highScoreRepository.findById(1L)).thenReturn(Optional.of(highScoreModel));
        HighScoreModel actual = highScoreService.deleteHighScore(1L);
        verify(highScoreRepository, times(1)).delete(highScoreModel);
        Assertions.assertNotNull(actual);
    }
}
