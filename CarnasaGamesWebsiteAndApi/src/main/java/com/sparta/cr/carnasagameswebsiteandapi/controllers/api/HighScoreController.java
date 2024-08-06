package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
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

@RestController
@RequestMapping("/api/high_scores")
public class HighScoreController {

    private HighScoreServiceImpl highScoreService;

    @Autowired
    public HighScoreController(HighScoreServiceImpl highScoreService){
        this.highScoreService = highScoreService;
    }

    @GetMapping("/search/all")
    public ResponseEntity<CollectionModel<EntityModel<HighScoreModel>>> getAllHighScores(){
        List<EntityModel<HighScoreModel>> highScores = highScoreService.getAllHighScores().stream().map(this::getHighScoreEntityModel).toList();
        return new ResponseEntity<>(CollectionModel.of(highScores).add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HighScoreController.class).getAllHighScores()).withSelfRel()
        ), HttpStatus.OK);


    }

    @GetMapping("/search/{scoreId}")
    public ResponseEntity<EntityModel<HighScoreModel>> getScoreById(@PathVariable Long scoreId){
        if(highScoreService.getHighScore(scoreId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        HighScoreModel highScoreModel = highScoreService.getHighScore(scoreId).get();
        return ResponseEntity.ok(getHighScoreEntityModel(highScoreModel).add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HighScoreController.class).getAllHighScores()).withRel("All Scores")
        ));
    }

    private Link getUserLink(HighScoreModel highScoreModel){
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(highScoreModel.getUserModel().getId())).withRel("User: " + highScoreModel.getUserModel().getUsername());
    }
    private Link getGameLink(HighScoreModel highScoreModel){
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).getGameById(highScoreModel.getGamesModel().getId())).withRel("Game: " + highScoreModel.getGamesModel().getTitle());
    }

    private EntityModel<HighScoreModel> getHighScoreEntityModel(HighScoreModel highScoreModel){
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HighScoreController.class).getScoreById(highScoreModel.getScoreId())).withSelfRel();
        return EntityModel.of(highScoreModel, getUserLink(highScoreModel), getGameLink(highScoreModel), selfLink);
    }
}
