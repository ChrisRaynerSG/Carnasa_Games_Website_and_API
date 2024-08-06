package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.CommentServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.GameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.HighScoreServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private GameServiceImpl gameService;
    private HighScoreServiceImpl highScoreService;
    private CommentServiceImpl commentService;

    @Autowired
    public GameController(GameServiceImpl gameService, HighScoreServiceImpl highScoreService, CommentServiceImpl commentService) {
        this.gameService = gameService;
        this.highScoreService = highScoreService;
        this.commentService = commentService;
    }

    @GetMapping("/search/all")
    public ResponseEntity<CollectionModel<EntityModel<GameModel>>> getAllGames(){
        List<EntityModel<GameModel>> allGames = gameService.getAllGames()
                .stream()
                .map(this::getGameEntityModel).toList();
        return new ResponseEntity<>(CollectionModel.of(allGames).add(
                WebMvcLinkBuilder.linkTo(methodOn(GameController.class).getAllGames()).withSelfRel()
        ), HttpStatus.OK);
    }
    @GetMapping("/search/{gameId}")
    public ResponseEntity<EntityModel<GameModel>> getGameById(@PathVariable("gameId") Long gameId){
        if(gameService.getGame(gameId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        GameModel gameModel = gameService.getGame(gameId).get();
        return new ResponseEntity<>(getGameEntityModel(gameModel), HttpStatus.OK);
    }

    private Link getGameCreator(GameModel gameModel){
        return WebMvcLinkBuilder.linkTo(methodOn(UserApiController.class).getUserById(gameModel.getCreator().getId())).withRel("Creator: " + gameModel.getCreator().getUsername());
    }

    private List<Link> getGameScores(GameModel gameModel){
        return highScoreService.getHighScoresByGame(gameModel.getId())
                .stream()
                .map(score
                        -> WebMvcLinkBuilder
                        .linkTo(methodOn(HighScoreController.class).getScoreById(score.getScoreId()))
                        .withRel("Score: " + score.getScore() + " User: " + score.getUserModel().getUsername())).toList();

    }

    private List<Link> getGameComments(GameModel gameModel){
        return commentService.getCommentsByGame(gameModel.getId())
                .stream().map(comment ->
                        WebMvcLinkBuilder
                                .linkTo(methodOn(CommentController.class).getCommentById(comment.getId()))
                                .withRel("User: " + comment.getUserModel().getUsername() + " Comment: " +comment.getCommentText()))
                .toList();
    }

    private EntityModel<GameModel> getGameEntityModel(GameModel gameModel){
        Link selfLink = WebMvcLinkBuilder.linkTo(methodOn(GameController.class).getGameById(gameModel.getId())).withSelfRel();
        return EntityModel.of(gameModel, selfLink, getGameCreator(gameModel)).add(getGameScores(gameModel)).add(getGameComments(gameModel));

    }
}
