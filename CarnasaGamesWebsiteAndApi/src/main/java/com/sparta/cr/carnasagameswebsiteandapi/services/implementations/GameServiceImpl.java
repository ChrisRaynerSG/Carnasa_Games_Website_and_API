package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.GameAlreadyExistsException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.InvalidGenreException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.InvalidTitleException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.NoUserException;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.GameRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.GameServicable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GameServiceImpl implements GameServicable {

    private final UserServiceImpl userServiceImpl;
    private final GameRepository gameRepository;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, UserServiceImpl userServiceImpl) {
        this.gameRepository = gameRepository;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public GameModel createGame(GameModel game) {
        if(validateNewGame(game)){
            game.setCreator(userServiceImpl.getUser(game.getCreator().getId()).get()); //isPresent done in validate game...
            game.setPublished(false);
            game.setTimesPlayed(0);
            return gameRepository.save(game);
        }
        else return null;
    }

    @Override
    public GameModel updateGame(GameModel game) {
        if(getGame(game.getId()).isEmpty()) {
            return null;
        }
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
    public List<GameModel> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public List<GameModel> getGamesByGenre(String genre) {
        return getAllGames().stream().filter(game -> game.getGenre().equalsIgnoreCase(genre)).toList();
    }

    @Override
    public List<GameModel> getGamesByTitle(String title) {
        return getAllGames().stream().filter(game -> game.getTitle().toLowerCase().contains(title.toLowerCase())).toList();
    }

    @Override
    public List<GameModel> getGamesByTitleAndGenre(String title, String genre) {
        return getGamesByGenre(genre).stream().filter(game -> game.getTitle().toLowerCase().contains(title.toLowerCase())).toList();
    }

    @Override
    @Transactional
    public List<GameModel> getGamesByCreatorId(Long creatorId) {
        if(userServiceImpl.getUser(creatorId).isEmpty()){
            return new ArrayList<>();
        }
        return getAllGames().stream().filter(gameModel -> Objects.equals(gameModel.getCreator().getId(), creatorId)).toList();
    }

    @Override
    @Transactional
    public List<GameModel> getGamesByCreatorUsername(String creatorName){
        return getAllGames().stream().filter(gameModel -> gameModel.getCreator().getUsername().toLowerCase().contains(creatorName.toLowerCase())).toList();
    }

    @Override
    public List<GameModel> getTopTenGames() {
        List<GameModel> games = getAllGames();
        games.sort(Comparator.comparingInt(GameModel::getTimesPlayed).reversed());
        return games.subList(0, Math.min(games.size(), 10));
    }

    @Override
    public List<GameModel> getTopTenGamesByGenre(String genre) {
        List<GameModel> games = new ArrayList<>(getGamesByGenre(genre));
        games.sort(Comparator.comparingInt(GameModel::getTimesPlayed).reversed());
        return games.subList(0, Math.min(games.size(), 10));
    }

    public boolean validateNewGame(GameModel game) {
        Matcher matcher = getGenreMatcher(game.getGenre());
        if(getGame(game.getId()).isPresent()){
            throw new GameAlreadyExistsException(game.getId().toString());
        }
        if(userServiceImpl.getUser(game.getCreator().getId()).isEmpty()){
            throw new NoUserException();
        }
        if(!matcher.matches()){
            throw new InvalidGenreException(game.getGenre());
        }
        if(!game.getTitle().matches("[a-zA-Z0-9\\s]+")){
            throw new InvalidTitleException(game.getTitle());
        }
        return true;
    }

    public GameModel increasePlaysByOne(GameModel game){
        game.setTimesPlayed(game.getTimesPlayed() + 1);
        return gameRepository.save(game);
        //might not need this but might be good for webController to increase every time page is clicked
    }

    public boolean validateExistingGame(GameModel game) {
        Matcher matcher = getGenreMatcher(game.getGenre());
        if(getGame(game.getId()).isEmpty()){
            return false; // gameNotFoundException already covered in Controller
        }
        GameModel beforeUpdate = getGame(game.getId()).get();
        if(beforeUpdate.getTimesPlayed()>game.getTimesPlayed()){
            return false; //cant decrease times played exception
        }

        if(!beforeUpdate.getTitle().equals(game.getTitle())){
            if(!game.getTitle().matches("[a-zA-Z0-9\\s]+")){
                throw new InvalidTitleException(game.getTitle());
            }
        }

        if(!beforeUpdate.getGenre().equals(game.getGenre())){
            if(!matcher.matches()){
                throw new InvalidGenreException(game.getGenre());
            }
        }

        if(!beforeUpdate.getCreator().getId().equals(game.getCreator().getId())){
            if(userServiceImpl.getUser(game.getCreator().getId()).isEmpty()){
                throw new NoUserException();
            }
        }
        return true;
    }

    public Matcher getGenreMatcher(String genre){
        String genreRegex = "\\b(Puzzle|Platformer|Shooter|Racing|Fighting|Sports|Adventure|Strategy|Simulation|Arcade)\\b";
        Pattern pattern = Pattern.compile(genreRegex, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(genre);
    }
}
