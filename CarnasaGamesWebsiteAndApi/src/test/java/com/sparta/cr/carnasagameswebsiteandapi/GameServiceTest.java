package com.sparta.cr.carnasagameswebsiteandapi;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.GameRepository;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.UserRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.GameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.GameServicable;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.UserServiceable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameServicable gameServicable;

    @InjectMocks
    private GameServiceImpl gameServiceImpl;

    private List<GameModel> gameModelList;
    private GameModel gameModel1;
    private GameModel gameModel2;

    @BeforeEach
    void setUp() {
        gameModel1 = new GameModel();
        gameModel2 = new GameModel();
        gameModelList = new LinkedList<>();



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
}
