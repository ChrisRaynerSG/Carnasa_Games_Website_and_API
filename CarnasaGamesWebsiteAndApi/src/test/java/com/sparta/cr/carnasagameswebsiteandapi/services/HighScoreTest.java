package com.sparta.cr.carnasagameswebsiteandapi.services;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.HighScoreRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.HighScoreServiceImpl;
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

import static org.mockito.Mockito.*;

@SpringBootTest
public class HighScoreTest {

    private static final Logger log = LoggerFactory.getLogger(HighScoreTest.class);
    @Mock
    HighScoreRepository highScoreRepository;

    @InjectMocks
    HighScoreServiceImpl highScoreService;

    private HighScoreModel highScore1;
    private HighScoreModel highScore2;
    private HighScoreModel highScore3;
    private List<HighScoreModel> highScoreModels;
    private List<HighScoreModel> topHighScoreModels;


    @BeforeEach
    void setUp() {
        highScore1 = new HighScoreModel();
        highScore2 = new HighScoreModel();
        highScore3 = new HighScoreModel();
        highScoreModels = new ArrayList<>();
        topHighScoreModels = new ArrayList<>();

        GameModel gameModel1 = new GameModel();
        GameModel gameModel2 = new GameModel();
        gameModel1.setId(1L);
        gameModel2.setId(2L);

        UserModel userModel1 = new UserModel();
        userModel1.setId(1L);
        UserModel userModel2 = new UserModel();
        userModel2.setId(2L);

        highScore1.setGamesModel(gameModel1);
        highScore2.setGamesModel(gameModel2);
        highScore3.setGamesModel(gameModel2);
        highScore1.setUserModel(userModel1);
        highScore2.setUserModel(userModel1);
        highScore3.setUserModel(userModel2);

        highScoreModels.add(highScore1);
        highScoreModels.add(highScore2);
        highScoreModels.add(highScore3);

        for (int i = 0; i <= 40; i++) {
            HighScoreModel highScoreModel = new HighScoreModel();
            if(i%2==0) {
                highScoreModel.setGamesModel(gameModel1);
            }
            else {
                highScoreModel.setGamesModel(gameModel2);
            }
            if(i%3==0) {
                highScoreModel.setUserModel(userModel1);
                highScoreModel.setDate(LocalDate.of(2000,12,25));
            }
            else {
                highScoreModel.setUserModel(userModel2);
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
        HighScoreModel actual = highScoreService.createHighScore(highScoreModel);
        Assertions.assertNull(actual);
    }
    @Test
    void createScoreReturnsScoreWhenScoreDoesNotExist(){
        HighScoreModel highScoreModel = new HighScoreModel();
        highScoreModel.setScoreId(1L);
        when(highScoreRepository.findById(1L)).thenReturn(Optional.empty());
        when(highScoreRepository.save(highScoreModel)).thenReturn(highScoreModel);
        HighScoreModel actual = highScoreService.createHighScore(highScoreModel);
        Assertions.assertNotNull(actual);
    }

    @Test
    void updateHighScoreReturnsNullWhenScoreDoesNotExist(){
        HighScoreModel highScoreModel = new HighScoreModel();
        highScoreModel.setScoreId(1L);
        when(highScoreRepository.findById(1L)).thenReturn(Optional.empty());
        HighScoreModel actual = highScoreService.updateHighScore(highScoreModel);
        Assertions.assertNull(actual);
    }
    @Test
    void updateHighScoreReturnsScoreWhenScoreExists(){
        HighScoreModel highScoreModel = new HighScoreModel();
        highScoreModel.setScoreId(1L);
        when(highScoreRepository.findById(1L)).thenReturn(Optional.of(highScoreModel));
        when(highScoreRepository.save(highScoreModel)).thenReturn(highScoreModel);
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
