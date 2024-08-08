package com.sparta.cr.carnasagameswebsiteandapi.services;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelAlreadyExistsException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UserNotFoundException;
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
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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

        FavouriteGameModelId favouriteGameModelId1 = new FavouriteGameModelId();
        favouriteGameModelId1.setGameId(1L);
        favouriteGameModelId1.setUserId(1L);

        favouriteGame1.setFavouriteGameModelId(favouriteGameModelId1);

        game1 = new GameModel();
        game2 = new GameModel();
        game3 = new GameModel();

        game1.setId(1L); game2.setId(2L); game3.setId(3L); game1.setTitle("Game1");
        game2.setTitle("Game2"); game3.setTitle("Game3"); game1.setTimesPlayed(1);

        favouriteGame1.setUserModel(user1);
        favouriteGame2.setUserModel(user1);
        favouriteGame3.setUserModel(user1);
        favouriteGame4.setUserModel(user2);

        favouriteGame1.setFavourite(true);
        favouriteGame2.setFavourite(true);
        favouriteGame3.setFavourite(false);
        favouriteGame4.setFavourite(false);

        favouriteGame1.setNumberOfVisits(1);
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
    @Test
    void testCreateFavouriteGameThrowsExceptionIfUserNotFound(){
        FavouriteGameModel game = favouriteGame1;
        when(userService.getUser(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> favouriteGameService.createFavouriteGame(game));
    }
    @Test
    void testCreateFavouriteGameThrowsExceptionIfGameNotFound(){
        FavouriteGameModel game = favouriteGame1;
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> favouriteGameService.createFavouriteGame(game),"Cannot create new Favourite Game relationship as no game with ID: " + game.getGameModel().getId().toString() + " could be found" );
    }
    @Test
    void testCreateFavouriteGameThrowsExceptionIfRelationshipAlreadyExists(){
        FavouriteGameModel game = favouriteGame1;
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        when(favouriteGameRepository.findById(any(FavouriteGameModelId.class))).thenReturn(Optional.of(favouriteGame1));
        assertThrows(ModelAlreadyExistsException.class, () -> favouriteGameService.createFavouriteGame(game));
    }
    @Test
    void testCreateFavouriteGameIsSuccessful(){
        FavouriteGameModel game = favouriteGame1;
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        when(favouriteGameRepository.findById(any(FavouriteGameModelId.class))).thenReturn(Optional.empty());
        when(favouriteGameRepository.save(any(FavouriteGameModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        FavouriteGameModel createdGame = favouriteGameService.createFavouriteGame(game);
        assertNotNull(createdGame);
        assertFalse(createdGame.isFavourite());
        assertEquals(2,game1.getTimesPlayed());
    }
    @Test
    void testUpdateFavouriteGameThrowsExceptionIfUserNotFound(){
        FavouriteGameModel game = favouriteGame1;
        when(userService.getUser(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> favouriteGameService.updateFavouriteGame(game));
    }
    @Test
    void testUpdateFavouriteGameThrowsExceptionIfGameNotFound(){
        FavouriteGameModel game = favouriteGame1;
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> favouriteGameService.updateFavouriteGame(game),"Cannot create new Favourite Game relationship as no game with ID: " + game.getGameModel().getId().toString() + " could be found" );
    }
    @Test
    void testUpdateFavouriteGameThrowsExceptionIfRelationshipDoesNotExists(){
        FavouriteGameModel game = favouriteGame1;
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        when(favouriteGameRepository.findById(any(FavouriteGameModelId.class))).thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> favouriteGameService.updateFavouriteGame(game));
    }
    @Test
    void testUpdateFavouriteGameDoesNotUpdateTimesPlayedIfTimesPlayedNotDifferent(){
        FavouriteGameModel game = favouriteGame1;
        game.setFavourite(false);
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        when(favouriteGameRepository.findById(any(FavouriteGameModelId.class))).thenReturn(Optional.of(favouriteGame1));
        when(favouriteGameRepository.save(any(FavouriteGameModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        FavouriteGameModel updatedRelationship = favouriteGameService.updateFavouriteGame(game);
        assertFalse(updatedRelationship.isFavourite());
        assertEquals(1,game1.getTimesPlayed());
    }
    @Test
    void testUpdateFavouriteGameDoesUpdateTimesPlayedIfTimesPlayedDifferent(){
        FavouriteGameModel game = new FavouriteGameModel();
        FavouriteGameModelId existingId = new FavouriteGameModelId();
        existingId.setGameId(game1.getId());
        existingId.setUserId(user1.getId());
        game.setFavouriteGameModelId(existingId);
        game.setGameModel(game1);
        game.setUserModel(user1);
        game.setFavourite(true);
        game.setNumberOfVisits(4);
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(gameService.getGame(1L)).thenReturn(Optional.of(game1));
        when(favouriteGameRepository.findById(any(FavouriteGameModelId.class))).thenReturn(Optional.of(favouriteGame1));
        when(favouriteGameRepository.save(any(FavouriteGameModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        FavouriteGameModel updatedRelationship = favouriteGameService.updateFavouriteGame(game);
        assertTrue(updatedRelationship.isFavourite());
        assertEquals(4,game1.getTimesPlayed());
    }
    @Test
    void testDeleteFavouriteGameThrowsExceptionIfUserNotFound(){
        when(favouriteGameRepository.findById(any(FavouriteGameModelId.class))).thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class,() -> favouriteGameService.deleteFavouriteGame(1L,1L));
    }
    @Test
    void testDeleteFavouriteGameSucessfullyDeletesFavouriteGame(){
        when(favouriteGameRepository.findById(any(FavouriteGameModelId.class))).thenReturn(Optional.of(favouriteGame1));
        FavouriteGameModel deletedGame = favouriteGameService.deleteFavouriteGame(1L,1L);
        assertNotNull(deletedGame);
    }
}
