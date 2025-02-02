package com.sparta.cr.carnasagameswebsiteandapi.services;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.GameAlreadyExistsException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.InvalidGenreException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.InvalidTitleException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidGameException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidUserException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelAlreadyExistsException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.GenreModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.GameRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.GameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GameServiceTest {

    private static final Logger log = LoggerFactory.getLogger(GameServiceTest.class);

    @Mock
    private GameRepository gameRepository;

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
    private GenreModel genreModel1;
    private GenreModel genreModel2;
    private Page<GameModel> page;

    @BeforeEach
    void setUp() {
        gameModel1 = new GameModel();
        gameModel2 = new GameModel();
        userModel1 = new UserModel();
        userModel2 = new UserModel();
        userModel3 = new UserModel();
        gameModelList = new ArrayList<>();
        topScoresList = new ArrayList<>();
        genreModel1 = new GenreModel();
        genreModel2 = new GenreModel();
        genreModel1.setGenre("Sports");
        genreModel2.setGenre("Puzzle");

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

        gameModel1.setGenre(genreModel1);
        gameModel2.setGenre(genreModel2);

        gameModel1.setCreator(userModel1);
        gameModel2.setCreator(userModel3);

        gameModel1.setTimesPlayed(1000);
        gameModel2.setTimesPlayed(2400);

        gameModel1.setTitle("Super Sports Game!");
        gameModel2.setTitle("Tricky Puzzle Game!");

        for(int i =0; i<10; i++){
            GameModel gameModel = new GameModel();
            if(i%2==0){
                gameModel.setGenre(genreModel1);
            }
            else {
                gameModel.setGenre(genreModel2);
            }
            gameModel.setTimesPlayed(i*10);
            topScoresList.add(gameModel);
        }
    }

    @Test
    void testGetAllGamesReturnsAllGames(){
        gameModelList.add(gameModel1);
        gameModelList.add(gameModel2);
        page = new PageImpl<>(gameModelList);
        int expected = 2;
        when(gameRepository.findAll(any(Pageable.class))).thenReturn(page);
        int actual = gameServiceImpl.getAllGames(0,10).toList().size();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetGamesByGenreReturnsGamesWithGenre(){
        gameModelList.add(gameModel1);
        page = new PageImpl<>(gameModelList);
        int expected = 1;
        when(gameRepository.findAllByGenre_Genre(eq("Sports"), any(Pageable.class))).thenReturn(page);
        int actual = gameServiceImpl.getGamesByGenre("Sports", 0, 10).toList().size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testGetGamesByTitleReturnsGamesWithPartialMatchTitle(){
        gameModelList.add(gameModel1);
        page = new PageImpl<>(gameModelList);
        int expected = 1;
        when(gameRepository.findAllByTitleContainingIgnoreCase(eq("su"), any(Pageable.class))).thenReturn(page);
        int actual = gameServiceImpl.getGamesByTitle("su",0,10).toList().size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testGetGamesByTitleAndGenreReturnsGamesThatMatch(){
        gameModelList.add(gameModel2);
        page = new PageImpl<>(gameModelList);
        int expected = 1;
        when(gameRepository.findByTitleContainingIgnoreCaseAndGenre_Genre(eq("puzzle"), eq("puzzle"), any(Pageable.class))).thenReturn(page);
        int actual = gameServiceImpl.getGamesByTitleAndGenre("puzzle", "puzzle", 0 ,10).toList().size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testGetGamesByCreator(){
        gameModelList.add(gameModel1);
        page = new PageImpl<>(gameModelList);
        int expected = 1;
        when(gameRepository.findByCreator_Id(eq(1234L), any(Pageable.class))).thenReturn(page);
        int actual = gameServiceImpl.getGamesByCreatorId(1234L, 0,10).toList().size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testGetGamesByCreatorNameAndUserMatchReturnsGamesThatMatch(){
        gameModelList.add(gameModel1);
        gameModelList.add(gameModel2);
        page = new PageImpl<>(gameModelList);
        int expected = 2;
        when(gameRepository.findByCreator_UsernameContainingIgnoreCase(eq("ad"),any(Pageable.class))).thenReturn(page);
        int actual = gameServiceImpl.getGamesByCreatorUsername("ad",0,2).toList().size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testGetTopTenGamesByTopClicksReturnsMostPlayedGames(){
        int expected = 10;
        page = new PageImpl<>(topScoresList);
        when(gameRepository.findAll(any(Pageable.class))).thenReturn(page);
        Page<GameModel> topGames = gameServiceImpl.getTopTenGames();
        Assertions.assertEquals(expected, topGames.toList().size());
    }
    @Test
    void testGetTopTenGamesByGenreReturnsMostPlayedGamesOfGenre(){
        int expected = 10;
        page = new PageImpl<>(topScoresList);
        when(gameRepository.findAllByGenre_Genre(eq("Puzzle"),any(Pageable.class))).thenReturn(page);
        Page<GameModel> topGames = gameServiceImpl.getTopTenGamesByGenre("Puzzle");
        Assertions.assertEquals(expected, topGames.toList().size());
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
    void testCreateNewGameThrowsExceptionIfGameAlreadyExists(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        gameModel.setGenre(genreModel1);
        gameModel.setTitle("Game Title");
        gameModel.setCreator(userModel1);
        when(gameRepository.findById(1234L)).thenReturn(Optional.of(gameModel));
        when(userServiceImpl.getUser(1234L)).thenReturn(Optional.of(userModel1));
        assertThrows(ModelAlreadyExistsException.class, () -> gameServiceImpl.createGame(gameModel));
    }
    @Test
    void testCreateNewGameThrowsExceptionIfCreatorDoesntExist(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        gameModel.setGenre(genreModel1);
        gameModel.setTitle("Game Title");
        gameModel.setCreator(userModel1);
        when(gameRepository.findById(1234L)).thenReturn(Optional.empty());
        when(userServiceImpl.getUser(1234L)).thenReturn(Optional.empty());
        assertThrows(InvalidUserException.class, () -> gameServiceImpl.createGame(gameModel));
    }
    @Test
    void testCreateNewGameThrowsExceptionIfGenreIsInvalid(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        GenreModel genreModel = new GenreModel();
        genreModel.setGenre("Action");
        gameModel.setGenre(genreModel);
        gameModel.setTitle("Game Title");
        gameModel.setCreator(userModel1);
        when(gameRepository.findById(1234L)).thenReturn(Optional.empty());
        when(userServiceImpl.getUser(1234L)).thenReturn(Optional.of(userModel1));
        assertThrows(InvalidGenreException.class, () -> gameServiceImpl.createGame(gameModel));
    }
    @Test
    void testCreateNewGameThrowsExceptionIfTitleIsInvalid(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        gameModel.setGenre(genreModel1);
        gameModel.setTitle("G^me-TitlE");
        gameModel.setCreator(userModel1);
        when(gameRepository.findById(1234L)).thenReturn(Optional.empty());
        when(userServiceImpl.getUser(1234L)).thenReturn(Optional.of(userModel1));
        assertThrows(InvalidTitleException.class, () -> gameServiceImpl.createGame(gameModel));
    }
    @Test
    void testCreateNewGameReturnsGameIfSuccessful(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        gameModel.setGenre(genreModel1);
        gameModel.setTitle("Game Title");
        gameModel.setCreator(userModel1);
        when(gameRepository.findById(1234L)).thenReturn(Optional.empty());
        when(userServiceImpl.getUser(1234L)).thenReturn(Optional.of(userModel1));
        when(gameRepository.save(any(GameModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        GameModel createdGame = gameServiceImpl.createGame(gameModel);
        verify(gameRepository, times(1)).save(gameModel);
        Assertions.assertNotNull(createdGame);
    }
    @Test
    void testUpdateGameThrowsExceptionIfGameDoesNotExist(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        gameModel.setGenre(genreModel1);
        gameModel.setTitle("Game Title");
        gameModel.setCreator(userModel1);
        gameModel.setTimesPlayed(1);
        when(gameRepository.findById(1234L)).thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> gameServiceImpl.updateGame(gameModel), "Cannot update game as ID: " + gameModel.getId() + " does not exist");
    }
    @Test
    void testUpdateGameThrowsExceptionIfTimesPlayedDecreases(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        gameModel.setGenre(genreModel1);
        gameModel.setTitle("Game Title");
        gameModel.setCreator(userModel1);
        gameModel.setTimesPlayed(100);
        when(gameRepository.findById(1234L)).thenReturn(Optional.of(gameModel1));
        assertThrows(InvalidGameException.class, ()-> gameServiceImpl.updateGame(gameModel), "Cannot update game with ID: " + gameModel.getId() + " times played cannot decrease");
    }
    @Test
    void testUpdateGameThrowsExceptionIfTitleIsInvalid(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        gameModel.setGenre(genreModel1);
        gameModel.setTitle("Game Title$£( SDFK;[[w");
        gameModel.setCreator(userModel1);
        gameModel.setTimesPlayed(10000);
        when(gameRepository.findById(1234L)).thenReturn(Optional.of(gameModel1));
        assertThrows(InvalidTitleException.class, ()-> gameServiceImpl.updateGame(gameModel));
    }
    @Test
    void testUpdateGameThrowsExceptionIfGenreIsInvalid(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        GenreModel genreModel = new GenreModel();
        genreModel.setGenre("Action");
        gameModel.setGenre(genreModel);
        gameModel.setTitle("Game Title");
        gameModel.setCreator(userModel1);
        gameModel.setTimesPlayed(10000);
        when(gameRepository.findById(1234L)).thenReturn(Optional.of(gameModel1));
        assertThrows(InvalidGenreException.class, ()-> gameServiceImpl.updateGame(gameModel));
    }
    @Test
    void testUpdateGameThrowsExceptionIfCreatorNotFound(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        gameModel.setGenre(genreModel1);
        gameModel.setTitle("Game Title");
        gameModel.setCreator(userModel1);
        gameModel.setTimesPlayed(10000);
        when(gameRepository.findById(1234L)).thenReturn(Optional.of(gameModel1));
        when(userServiceImpl.getUser(anyLong())).thenReturn(Optional.empty());
        assertThrows(InvalidUserException.class, ()-> gameServiceImpl.updateGame(gameModel), "cannot update game with no creator");
    }
    @Test
    void testUpdateGameReturnsGameIfSuccessful(){
        GameModel gameModel = new GameModel();
        gameModel.setId(1234L);
        gameModel.setGenre(genreModel1);
        gameModel.setCreator(userModel1);
        gameModel.setTitle("Game Title");
        gameModel.setTimesPlayed(10000);
        when(gameRepository.findById(1234L)).thenReturn(Optional.of(gameModel1));
        when(userServiceImpl.getUser(anyLong())).thenReturn(Optional.of(userModel1));
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
