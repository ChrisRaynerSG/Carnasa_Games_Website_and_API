package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.annotations.CurrentOwner;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UserNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.CommentModel;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.CommentServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.GameServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/carnasa-game-api/v1/comments")
public class CommentApiController {

    private final CommentServiceImpl commentService;
    private final UserServiceImpl userService;
    private final GameServiceImpl gameService;

    @Autowired
    public CommentApiController(CommentServiceImpl commentService, UserServiceImpl userService, GameServiceImpl gameService) {
        this.commentService = commentService;
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/search/all")
    public ResponseEntity<CollectionModel<EntityModel<CommentModel>>> getAllComments(@CurrentOwner String username,
                                                                                     Authentication authentication) {
        List<EntityModel<CommentModel>> comments = commentService.getAllComments()
                .stream().map(comment -> getCommentEntityModel(comment, username, authentication)).toList();
        return new ResponseEntity<>(CollectionModel.of(comments).add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CommentApiController.class).getAllComments(username,authentication)).withSelfRel())
                , HttpStatus.OK);
    }
    @GetMapping("/search/{commentId}")
    public ResponseEntity<EntityModel<CommentModel>> getCommentById(@PathVariable Long commentId,
                                                                    @CurrentOwner String username,
                                                                    Authentication authentication) {
        if(commentService.getComment(commentId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CommentModel commentModel = commentService.getComment(commentId).get();
        return ResponseEntity.ok(getCommentEntityModel(commentModel,username,authentication));
    }
    @GetMapping("/search/game/{gameId}")
    public ResponseEntity<CollectionModel<EntityModel<CommentModel>>> getAllCommentsByGame(@PathVariable Long gameId,
                                                                                           @CurrentOwner String username,
                                                                                           Authentication authentication) {
        if(gameService.getGame(gameId).isEmpty()) {
            throw new ModelNotFoundException("Game with ID: " + gameId + " not found");
        }
       List<EntityModel<CommentModel>> comments = commentService.getCommentsByGame(gameId).stream().map(
               comment -> getCommentEntityModel(comment, username, authentication)
       ).toList();
       if(comments.isEmpty()) {
           return ResponseEntity.notFound().build();
       }
       return new ResponseEntity<>(CollectionModel.of(comments), HttpStatus.OK);
    }
    @GetMapping("/search/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<CommentModel>>> getAllCommentsByUser(@PathVariable Long userId,
                                                                                           @CurrentOwner String username,
                                                                                           Authentication authentication) {
        if(userService.getUser(userId).isEmpty()) {
            throw new UserNotFoundException(userId.toString());
        }
        List<EntityModel<CommentModel>> comments = commentService.getCommentsByUser(userId).stream().map(
                comment -> getCommentEntityModel(comment, username, authentication)).toList();
        if(comments.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(CollectionModel.of(comments), HttpStatus.OK);
    }
    @GetMapping("/search/date/today")
    public ResponseEntity<CollectionModel<EntityModel<CommentModel>>> getAllCommentsByDateToday(@CurrentOwner String username,
                                                                                                Authentication authentication) {
        LocalDate today = LocalDate.now();
        List<EntityModel<CommentModel>> comments = commentService.getCommentsFromToday(today).stream().map(
                comment -> getCommentEntityModel(comment,username,authentication)).toList();
        if(comments.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(CollectionModel.of(comments), HttpStatus.OK);
    }
    @GetMapping("/search/date/{date1}/{date2}")
    public ResponseEntity<CollectionModel<EntityModel<CommentModel>>> getAllCommentsByDate(@PathVariable String date1,
                                                                                           @PathVariable String date2,
                                                                                           @CurrentOwner String username,
                                                                                           Authentication authentication) {

        List<EntityModel<CommentModel>> comments = commentService.getCommentsByDate(date1, date2).stream().map(
                comment -> getCommentEntityModel(comment, username, authentication)).toList();
        if(comments.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(CollectionModel.of(comments), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<EntityModel<CommentModel>> createComment(@RequestBody CommentModel commentModel,
                                                                   @CurrentOwner String username,
                                                                   Authentication authentication) {
        if(!commentService.validateNewComment(commentModel)){
            return ResponseEntity.badRequest().build();
        }
        CommentModel newComment = commentService.createComment(commentModel);
        URI location = URI.create("/api/comments/search/"+newComment.getId());
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CommentApiController.class).getCommentById(newComment.getId(), username, authentication)).withSelfRel();
        return ResponseEntity.created(location).body(EntityModel.of(newComment).add(selfLink));
    }
    @PutMapping("/update/{commentId}")
    public ResponseEntity updateComment(@PathVariable Long commentId, @RequestBody CommentModel commentModel,
                                        @CurrentOwner String username, Authentication authentication) {
        if(commentService.getComment(commentId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if(!commentModel.getId().equals(commentId)) {
            return ResponseEntity.badRequest().build();
        }
        if(!commentService.validateExistingComment(commentModel)) {
            return ResponseEntity.badRequest().build();
        }
        commentService.updateComment(commentModel);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity deleteComment(@PathVariable Long commentId, @CurrentOwner String username,
                                        Authentication authentication) {
        if(commentService.getComment(commentId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    private Link getUserLink(CommentModel commentModel, String currentUser, Authentication authentication){
        if(isUserAccountPrivateAndHidden(commentModel,currentUser,authentication)){
            return Link.of("/").withRel("Private user");
        }
        else {
            return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                            .methodOn(UserApiController.class)
                            .getUserById(commentModel.getUserModel().getId(),currentUser,authentication))
                    .withRel("User: " + commentModel.getUserModel().getUsername());
        }
    }

    private Link getGameLink(CommentModel commentModel, String currentUser, Authentication authentication){
        return WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                .methodOn(GameApiController.class).getGameById(commentModel.getGamesModel().getId(), currentUser, authentication))
                .withRel("Game: " + commentModel.getGamesModel().getTitle());
    }

    private EntityModel<CommentModel> getCommentEntityModel(CommentModel commentModel, String currentUser, Authentication authentication) {
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CommentApiController.class).getCommentById(commentModel.getId(), currentUser, authentication)).withSelfRel();
        Link relink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CommentApiController.class).getAllComments(currentUser,authentication)).withRel("All Comments");
        return EntityModel.of(commentModel, selfLink, relink, getUserLink(commentModel, currentUser, authentication), getGameLink(commentModel, currentUser, authentication));
    }

    private boolean isUserAccountPrivateAndHidden(CommentModel commentModel, String currentUser, Authentication authentication){
        return commentModel.getUserModel().isPrivate()
                && authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))
                && !commentModel.getUserModel().getUsername().equals(currentUser);

    }
}
