package com.sparta.cr.carnasagameswebsiteandapi;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.GameRepository;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.UserRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.GameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.GameServicable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameServicable gameServicable;

    @InjectMocks
    private GameServiceImpl gameServiceImpl;

    private List<GameModel> gameModelList;
    private GameModel gameModel1;
    private GameModel gameModel2;
    private UserModel userModel1;

    @BeforeEach
    void setUp() {
        gameModel1 = new GameModel();
        gameModel2 = new GameModel();
        gameModelList = new LinkedList<>();

        userModel1.setUsername("admin");
        userModel1.setId(1234L);
        userModel1.setPassword("admin");
        userModel1.setEmail("admin@admin.com");

        gameModel1.setGenre("action");
        gameModel2.setGenre("puzzle");

        gameModel1.setCreator(userModel1);

        gameModel1.setTitle("Super Action Game!");
        gameModel2.setTitle("Tricky Puzzle Game!");

        gameModelList.add(gameModel1);
        gameModelList.add(gameModel2);
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
        when(userRepository.findById(userModel1.getId())).thenReturn(Optional.of(userModel1));
        int actual = gameServiceImpl.getGamesByCreatorId(1234L).size();
        Assertions.assertEquals(expected, actual);
    }
}
