package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.config.CensorConfig;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.InvalidGenreException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.InvalidTitleException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidGameException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidUserException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelAlreadyExistsException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.GameRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.GameServicable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GameServiceImpl implements GameServicable {

    private final UserServiceImpl userServiceImpl;
    private final GameRepository gameRepository;
    private final CensorConfig censorConfig;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, UserServiceImpl userServiceImpl, CensorConfig censorConfig) {
        this.gameRepository = gameRepository;
        this.userServiceImpl = userServiceImpl;
        this.censorConfig = censorConfig;
    }

    @Override
    public GameModel createGame(GameModel game) {
        validateNewGame(game);
        game.setCreator(userServiceImpl.getUser(game.getCreator().getId()).get()); //isPresent done in validate game...
        game.setPublished(false);
        game.setTimesPlayed(0);
        return gameRepository.save(game);
    }

    @Override
    public GameModel updateGame(GameModel game) {
        validateExistingGame(game);
        return gameRepository.save(game);
    }

    @Override
    public GameModel deleteGame(Long gameId) {
        if(gameRepository.findById(gameId).isPresent()) {
            GameModel game = gameRepository.findById(gameId).get();
            gameRepository.delete(game);
            return game;
        }
        return null;
    }

    @Override
    public Optional<GameModel> getGame(Long gameId) {
        return gameRepository.findById(gameId);
    }

    @Override
    public Page<GameModel> getAllGames(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return gameRepository.findAll(pageable);
    }

    @Override
    public Page<GameModel> getGamesByGenre(String genre, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return gameRepository.findAllByGenre_Genre(genre, pageable);
    }

    @Override
    public Page<GameModel> getGamesByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return gameRepository.findAllByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    public Page<GameModel> getGamesByTitleAndGenre(String title, String genre, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return gameRepository.findByTitleContainingIgnoreCaseAndGenre_Genre(title, genre, pageable);
    }

    @Override
    @Transactional
    public Page<GameModel> getGamesByCreatorId(Long creatorId, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return gameRepository.findByCreator_Id(creatorId, pageable);
    }

    @Override
    @Transactional
    public Page<GameModel> getGamesByCreatorUsername(String creatorName, int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        return gameRepository.findByCreator_UsernameContainingIgnoreCase(creatorName,pageable);
    }

    @Override
    public Page<GameModel> getTopTenGames() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by( "timesPlayed").descending());
        return gameRepository.findAll(pageable);
//        List<GameModel> games = getAllGames();
//        games.sort(Comparator.comparingInt(GameModel::getTimesPlayed).reversed());
//        return games.subList(0, Math.min(games.size(), 10));
    }

    @Override
    public Page<GameModel> getTopTenGamesByGenre(String genre) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("timesPlayed").descending());
        return gameRepository.findAllByGenre_Genre(genre,pageable);
//        List<GameModel> games = new ArrayList<>(getGamesByGenre(genre));
//        games.sort(Comparator.comparingInt(GameModel::getTimesPlayed).reversed());
//        return games.subList(0, Math.min(games.size(), 10));
    }

    public boolean validateNewGame(GameModel game) {
        Matcher matcher = getGenreMatcher(game.getGenre().getGenre());
        if(getGame(game.getId()).isPresent()){
            throw new ModelAlreadyExistsException("Cannot create new game with ID: " + game.getId() + " already exists");
        }
        if(userServiceImpl.getUser(game.getCreator().getId()).isEmpty()){
            throw new InvalidUserException("Cannot create game without creator");
        }
        if(!matcher.matches()){
            throw new InvalidGenreException(game.getGenre().getGenre());
        }
        if(!game.getTitle().matches("[a-zA-Z0-9\\s]+")){
            throw new InvalidTitleException(game.getTitle());
        }
        if(censorConfig.censorBadText(game.getTitle()).contains("*")){
            throw new InvalidUserException("Please choose a title that does not contain inappropriate language");
        }
        if(censorConfig.censorBadText(game.getDescription()).contains("*")){
            throw new InvalidUserException("Game description may not contain inappropriate language");
        }
        return true;
    }

    public GameModel increasePlaysByOne(GameModel game){
        game.setTimesPlayed(game.getTimesPlayed() + 1);
        return gameRepository.save(game);
        //might not need this but might be good for webController to increase every time page is clicked
    }

    public boolean validateExistingGame(GameModel game) {
        Matcher matcher = getGenreMatcher(game.getGenre().getGenre());
        if(getGame(game.getId()).isEmpty()){
            throw new ModelNotFoundException("Cannot update game as ID: " + game.getId() + " does not exist");
        }
        GameModel beforeUpdate = getGame(game.getId()).get();
        if(beforeUpdate.getTimesPlayed()>game.getTimesPlayed()){
            throw new InvalidGameException("Cannot update game with ID: " + game.getId() + " times played cannot decrease");
        }

        if(!beforeUpdate.getTitle().equals(game.getTitle())){
            if(!game.getTitle().matches("[a-zA-Z0-9\\s]+")){
                throw new InvalidTitleException(game.getTitle());
            }
        }
        if(!beforeUpdate.getGenre().equals(game.getGenre())){
            if(!matcher.matches()){
                throw new InvalidGenreException(game.getGenre().getGenre());
            }
        }
        if(userServiceImpl.getUser(game.getCreator().getId()).isEmpty()){
            throw new InvalidUserException("cannot update game with no creator");
        }

        if(censorConfig.censorBadText(game.getTitle()).contains("*")){
            throw new InvalidUserException("Please choose a title that does not contain inappropriate language");
        }

        if(censorConfig.censorBadText(game.getDescription()).contains("*")){
            throw new InvalidUserException("Game description may not contain inappropriate language");
        }
        return true;
    }

    public Matcher getGenreMatcher(String genre){
        String genreRegex = "\\b(Puzzle|Platformer|Shooter|Racing|Fighting|Sports|Adventure|Strategy|Simulation|Arcade)\\b";
        Pattern pattern = Pattern.compile(genreRegex, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(genre);
    }
}
