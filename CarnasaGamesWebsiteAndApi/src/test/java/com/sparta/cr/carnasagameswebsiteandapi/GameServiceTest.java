package com.sparta.cr.carnasagameswebsiteandapi;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.GameRepository;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.UserRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.GameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.GameServicable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GameServiceTest {

    private static final Logger log = LoggerFactory.getLogger(GameServiceTest.class);
    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameServicable gameServicable;

    @Mock
    private UserServiceImpl userServiceImpl;

    @InjectMocks
    private GameServiceImpl gameServiceImpl;

    private List<GameModel> gameModelList;
    private List<GameModel> topScoresList;
    private GameModel gameModel1;
    private GameModel gameModel2;
    private UserModel userModel1;
    private UserModel userModel2;
    private UserModel userModel3;

    @BeforeEach
    void setUp() {
        gameModel1 = new GameModel();
        gameModel2 = new GameModel();
        userModel1 = new UserModel();
        userModel2 = new UserModel();
        userModel3 = new UserModel();
        gameModelList = new ArrayList<>();
        topScoresList = new ArrayList<>();

        userModel1.setUsername("admin");
        userModel1.setId(1234L);
        userModel1.setPassword("admin");
        userModel1.setEmail("admin@admin.com");

        userModel2.setUsername("user");
        userModel2.setId(2345L);
        userModel2.setPassword("user");
        userModel2.setEmail("user@user.com");

        userModel3.setUsername("admoo");
        userModel3.setId(3456L);
        userModel3.setPassword("admoo");
        userModel3.setEmail("user2@user2.com");

        gameModel1.setGenre("action");
        gameModel2.setGenre("puzzle");

        gameModel1.setCreator(userModel1);
        gameModel2.setCreator(userModel3);

        gameModel1.setTimesPlayed(1000);
        gameModel2.setTimesPlayed(2400);

        gameModel1.setTitle("Super Action Game!");
        gameModel2.setTitle("Tricky Puzzle Game!");

        gameModelList.add(gameModel1);
        gameModelList.add(gameModel2);

        for(int i =0; i<30; i++){
            GameModel gameModel = new GameModel();
            if(i%2==0){
                gameModel.setGenre("puzzle");
            }
            else {
                gameModel.setGenre("action");
            }
            gameModel.setTimesPlayed(i*10);
            topScoresList.add(gameModel);
        }
    }

    @Test
    void testGetAllGamesReturnsAllGames(){
        int expected = 2;
        when(gameRepository.findAll()).thenReturn(gameModelList);
        int actual = gameServiceImpl.getAllGames().size();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetGamesByGenreReturnsGamesWithGenre(){
        int expected = 1;
        when(gameRepository.findAll()).thenReturn(gameModelList);
        int actual = gameServiceImpl.getGamesByGenre("action").size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testGetGamesByTitleReturnsGamesWithPartialMatchTitle(){
        int expected = 1;
        when(gameRepository.findAll()).thenReturn(gameModelList);
        int actual = gameServiceImpl.getGamesByTitle("su").size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testGetGamesByTitleAndGenreReturnsGamesThatMatch(){
        int expected = 1;
        when(gameRepository.findAll()).thenReturn(gameModelList);
        int actual = gameServiceImpl.getGamesByTitleAndGenre("action", "action").size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testGetGamesByCreator(){
        int expected = 1;

        when(gameRepository.findAll()).thenReturn(gameModelList);
        when(userServiceImpl.getUser(1234L)).thenReturn(Optional.of(userModel1));

        int actual = gameServiceImpl.getGamesByCreatorId(1234L).size();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetGamesByCreatorAndNoCreatorMatchReturnsEmptyList(){
        int expected = 0;
        when(gameRepository.findAll()).thenReturn(gameModelList);
        when(userServiceImpl.getUser(1234L)).thenReturn(Optional.empty());
        int actual = gameServiceImpl.getGamesByCreatorId(1234L).size();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetGamesByCreatorNameAndNoCreatorMatchReturnsEmptyList(){
        int expected = 0;
        when(gameRepository.findAll()).thenReturn(gameModelList);
        when(userServiceImpl.getUserByUsername("nobody")).thenReturn(Optional.empty());
        int actual = gameServiceImpl.getGamesByCreatorUsername("nobody").size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testGetGamesByCreatorNameAndUserMatchReturnsGamesThatMatch(){
        int expected = 2;
        when(gameRepository.findAll()).thenReturn(gameModelList);
        when(userServiceImpl.getUserByUsername("ad")).thenReturn(Optional.of(userModel1));
        int actual = gameServiceImpl.getGamesByCreatorUsername("ad").size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testGetTopTenGamesByTopClicksReturnsMostPlayedGames(){
        int expected = 10;
        when(gameRepository.findAll()).thenReturn(topScoresList);
        List<GameModel> topGames = gameServiceImpl.getTopTenGames();
        for(GameModel gameModel : topGames){
            log.atInfo().log(gameModel.toString());
        }
        Assertions.assertEquals(expected, topGames.size());
    }
    @Test
    void testGetTopTenGamesReturnsLessThanTenIfLessThanTenGamesExist(){
        int expected = 2;
        when(gameRepository.findAll()).thenReturn(gameModelList);
        List<GameModel> topGames = gameServiceImpl.getTopTenGames();
        for(GameModel gameModel : topGames){
            log.atInfo().log(gameModel.toString());
        }
        Assertions.assertEquals(expected, topGames.size());
    }
    @Test
    void testGetTopTenGamesByGenreReturnsMostPlayedGamesOfGenre(){
        int expected = 10;
        when(gameRepository.findAll()).thenReturn(topScoresList);
        List<GameModel> topGames = gameServiceImpl.getTopTenGamesByGenre("action");
        for(GameModel gameModel : topGames){
            log.atInfo().log(gameModel.toString());
        }
        Assertions.assertEquals(expected, topGames.size());
    }
    @Test
    void testWhenGetGameReturnGameIfGameExists(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        when(gameRepository.findById(1234L)).thenReturn(Optional.of(gameModel));
        Assertions.assertNotNull(gameServiceImpl.getGame(1234L));
    }
    @Test
    void testWhenGetGameReturnEmptyOptionalIfGameDoesNotExist(){
        when(gameRepository.findById(1234L)).thenReturn(Optional.empty());
        Optional<GameModel> gameOptional = gameServiceImpl.getGame(1234L);
        Assertions.assertTrue(gameOptional.isEmpty());
    }
    @Test
    void testCreateNewGameReturnsNullIfGameAlreadyExists(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        when(gameRepository.findById(1234L)).thenReturn(Optional.of(gameModel1));
        GameModel createdGame = gameServiceImpl.createGame(gameModel);
        Assertions.assertNull(createdGame);
    }
    @Test
    void testCreateNewGameReturnsGameIfSuccessful(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        gameModel.setGenre("action");
        gameModel.setCreator(userModel1);
        when(gameRepository.findById(1234L)).thenReturn(Optional.empty());
        when(gameRepository.save(any(GameModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        GameModel createdGame = gameServiceImpl.createGame(gameModel);
        verify(gameRepository, times(1)).save(any(GameModel.class));
        Assertions.assertNotNull(createdGame);
    }
    @Test
    void testUpdateGameReturnsNullIfGameDoesNotExist(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        when(gameRepository.findById(1234L)).thenReturn(Optional.empty());
        GameModel udpatedGame = gameServiceImpl.updateGame(gameModel);
        Assertions.assertNull(udpatedGame);
    }
    @Test
    void testUpdateGameReturnsGameIfSuccessful(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        gameModel.setGenre("puzzle");
        gameModel.setCreator(userModel1);
        when(gameRepository.findById(1234L)).thenReturn(Optional.of(gameModel1));
        when(gameRepository.save(any(GameModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        GameModel updatedGame = gameServiceImpl.updateGame(gameModel);
        verify(gameRepository, times(1)).save(any(GameModel.class));
        Assertions.assertEquals(updatedGame.getGenre(), gameModel.getGenre());
    }
    @Test
    void testDeleteGameReturnsNullIfGameDoesNotExist(){
        when(gameRepository.findById(1234L)).thenReturn(Optional.empty());
        GameModel gameModel = gameServiceImpl.deleteGame(1234L);
        Assertions.assertNull(gameModel);
    }
    @Test
    void testDeleteGameReturnsGameAndDeletesIfGameExists(){
        when(gameRepository.findById(1234L)).thenReturn(Optional.of(gameModel1));
        GameModel gameModel = gameServiceImpl.deleteGame(1234L);
        verify(gameRepository, times(1)).delete(gameModel1);
        Assertions.assertNotNull(gameModel);
    }
}
