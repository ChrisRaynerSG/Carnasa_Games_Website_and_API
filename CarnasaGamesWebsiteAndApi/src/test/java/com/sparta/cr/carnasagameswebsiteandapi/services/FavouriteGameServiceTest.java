package com.sparta.cr.carnasagameswebsiteandapi.services;

import com.sparta.cr.carnasagameswebsiteandapi.models.FavouriteGameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.FavouriteGameModelId;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.FavouriteGameRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.FavouriteGameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.GameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FavouriteGameServiceTest {

    private static final Logger log = LoggerFactory.getLogger(FavouriteGameServiceTest.class);
    @Mock
    FavouriteGameRepository favouriteGameRepository;
    @Mock
    UserServiceImpl userService;
    @Mock
    GameServiceImpl gameService;
    @InjectMocks
    FavouriteGameServiceImpl favouriteGameService;

    private FavouriteGameModel favouriteGame1;
    private FavouriteGameModel favouriteGame2;
    private FavouriteGameModel favouriteGame3;
    private FavouriteGameModel favouriteGame4;

    private UserModel user1;
    private UserModel user2;

    private GameModel game1;
    private GameModel game2;
    private GameModel game3;

    private List<FavouriteGameModel> favouriteGamesList;

    @BeforeEach
    void setUp() {

        favouriteGame1 = new FavouriteGameModel();
        favouriteGame2 = new FavouriteGameModel();
        favouriteGame3 = new FavouriteGameModel();
        favouriteGame4 = new FavouriteGameModel();

        user1 = new UserModel();
        user2 = new UserModel();
        user1.setId(1L);
        user2.setId(2L);
        user1.setUsername("User1");
        user2.setUsername("User2");

        game1 = new GameModel();
        game2 = new GameModel();
        game3 = new GameModel();

        game1.setId(1L); game2.setId(2L); game3.setId(3L); game1.setTitle("Game1");
        game2.setTitle("Game2"); game3.setTitle("Game3");

        favouriteGame1.setUserModel(user1);
        favouriteGame2.setUserModel(user1);
        favouriteGame3.setUserModel(user1);
        favouriteGame4.setUserModel(user2);

        favouriteGame1.setFavourite(true);
        favouriteGame2.setFavourite(true);
        favouriteGame3.setFavourite(false);
        favouriteGame4.setFavourite(false);

        favouriteGame1.setNumberOfVisits(14);
        favouriteGame2.setNumberOfVisits(2591);
        favouriteGame3.setNumberOfVisits(13);
        favouriteGame4.setNumberOfVisits(14);

        favouriteGame1.setGameModel(game1);
        favouriteGame2.setGameModel(game2);
        favouriteGame3.setGameModel(game3);
        favouriteGame4.setGameModel(game2);

        favouriteGamesList = new ArrayList<>();
        favouriteGamesList.add(favouriteGame1);
        favouriteGamesList.add(favouriteGame2);
        favouriteGamesList.add(favouriteGame3);
        favouriteGamesList.add(favouriteGame4);

        when(favouriteGameRepository.findAll()).thenReturn(favouriteGamesList);
    }

    @Test
    void testGetFavouriteGame() {
        FavouriteGameModel expected = favouriteGame1;
        when(favouriteGameRepository.findById(any(FavouriteGameModelId.class))).thenReturn(Optional.of(favouriteGame1));
        FavouriteGameModel actual = favouriteGameService.getFavouriteGame(new FavouriteGameModelId()).get();
        assertEquals(expected, actual);
    }
    @Test
    void testGetAllFavouriteGames() {
        int expected = 4;
        int actual = favouriteGameService.getAllFavouriteGames().size();
        assertEquals(expected, actual);
    }
    @Test
    void testGetAllGamesPlayedByUserId(){
        int expected = 3;
        int actual = favouriteGameService.getAllGamesPlayedByUserId(1L).size();
        assertEquals(expected, actual);
    }
    @Test
    void testGetAllFavouriteGamesByUserId(){
        int expected = 2;
        int actual = favouriteGameService.getAllFavouriteGamesByUserId(1L).size();
        assertEquals(expected, actual);
    }
    @Test
    void testGetTopTenFavouriteGamesByUserId(){
        int expected = 3;
        List<FavouriteGameModel> top10games = favouriteGameService.getTopTenFavouriteGamesByUserId(1L);
        for(FavouriteGameModel game: top10games){
            log.atInfo().setMessage(game.toString()).log();
        }
        assertEquals(expected, top10games.size());
    }
    // todo: add tests for creation and updating of favourite game relationships
}
