package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelAlreadyExistsException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UserNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.FavouriteGameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.FavouriteGameModelId;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.FavouriteGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FavouriteGameImpl {

    private final FavouriteGameRepository favouriteGameRepo;
    private final GameServiceImpl gameService;
    private final UserServiceImpl userService;

    @Autowired
    public FavouriteGameImpl(FavouriteGameRepository favouriteGameRepo, GameServiceImpl gameService, UserServiceImpl userService) {
        this.favouriteGameRepo = favouriteGameRepo;
        this.gameService = gameService;
        this.userService = userService;
    }

    public Optional<FavouriteGameModel> getFavouriteGame(FavouriteGameModelId favouriteGameModelId){
        return favouriteGameRepo.findById(favouriteGameModelId);
    }

    public List<FavouriteGameModel> getAllFavouriteGames(){
        return favouriteGameRepo.findAll();
    }

    public List<FavouriteGameModel> getAllGamesPlayedByUserId(Long userId){
       return getAllFavouriteGames().stream().filter(fav -> fav.getUserModel().getId().equals(userId)).toList();
    }

    public List<FavouriteGameModel> getAllFavouriteGamesByUserId(Long userId){
        return getAllGamesPlayedByUserId(userId).stream().filter(FavouriteGameModel::isFavourite).toList();
    }

    public List<FavouriteGameModel> getTopTenFavouriteGamesByUserId(Long userId){
        List<FavouriteGameModel> allGamesPlayed = new ArrayList<>(getAllGamesPlayedByUserId(userId));
        allGamesPlayed.sort(Comparator.comparingInt(FavouriteGameModel::getNumberOfVisits).reversed());
        return allGamesPlayed.subList(0, Math.min(allGamesPlayed.size(), 10));
    }

    public FavouriteGameModel createFavouriteGame(FavouriteGameModel favouriteGameModel){
        if(validateNewFavouriteGame(favouriteGameModel)){
            updateGameTimesPlayed(favouriteGameModel, 1);
            favouriteGameModel.setNumberOfVisits(1);
            favouriteGameModel.setFavourite(false);
            return favouriteGameRepo.save(favouriteGameModel);

        }
        return null;
    }

    public FavouriteGameModel updateFavouriteGame(FavouriteGameModel favouriteGameModel){
        if(validateExistingFavouriteGame(favouriteGameModel)){
            FavouriteGameModel preUpdate = getFavouriteGame(favouriteGameModel.getFavouriteGameModelId()).get();
            if(preUpdate.getNumberOfVisits() == favouriteGameModel.getNumberOfVisits()){
                return favouriteGameRepo.save(favouriteGameModel);
            }
            else {
                int diff = favouriteGameModel.getNumberOfVisits() - preUpdate.getNumberOfVisits();
                updateGameTimesPlayed(favouriteGameModel, diff);
                return favouriteGameRepo.save(favouriteGameModel);
            }
        }
        return null;
    }

    public FavouriteGameModel deleteFavouriteGame(FavouriteGameModelId favouriteGameModelId){
        if(getFavouriteGame(favouriteGameModelId).isPresent()){
            FavouriteGameModel relDeleted = getFavouriteGame(favouriteGameModelId).get();
            favouriteGameRepo.deleteById(favouriteGameModelId);
            return relDeleted;
        }
        return null;
    }

    private void updateGameTimesPlayed(FavouriteGameModel favouriteGameModel, int diff) {
        GameModel game = gameService.getGame(favouriteGameModel.getGameModel().getId()).get();
        game.setTimesPlayed(game.getTimesPlayed()+ diff);
        gameService.updateGame(game);
    }

    public boolean validateNewFavouriteGame(FavouriteGameModel favouriteGameModel){
        if(validateUserAndGame(favouriteGameModel)){
            if(getFavouriteGame(favouriteGameModel.getFavouriteGameModelId()).isPresent()){
                throw new ModelAlreadyExistsException("Cannot create new favourite game relationship as already exists");
            }
            return true;
        }
        else return false;
    }

    public boolean validateExistingFavouriteGame(FavouriteGameModel favouriteGameModel){
        if(validateUserAndGame(favouriteGameModel)){
            if(getFavouriteGame(favouriteGameModel.getFavouriteGameModelId()).isEmpty()){
                throw new ModelNotFoundException("Cannot update Favourite Game relationship as relationship not found");
            }
            return true;
        }
        else return false;
    }

    private boolean validateUserAndGame(FavouriteGameModel favouriteGameModel) {
        if(userService.getUser(favouriteGameModel.getUserModel().getId()).isEmpty()
                ||userService.getUser(favouriteGameModel.getFavouriteGameModelId().getUserId()).isEmpty()){
            throw new UserNotFoundException(favouriteGameModel.getUserModel().getId().toString());
        }
        if(gameService.getGame(favouriteGameModel.getGameModel().getId()).isEmpty()
                ||gameService.getGame(favouriteGameModel.getFavouriteGameModelId().getGameId()).isEmpty()){
            throw new ModelNotFoundException("Cannot create new Favourite Game relationship as no game with ID: " + favouriteGameModel.getGameModel().getId().toString() + " could be found");
        }
        return true;
    }
}
