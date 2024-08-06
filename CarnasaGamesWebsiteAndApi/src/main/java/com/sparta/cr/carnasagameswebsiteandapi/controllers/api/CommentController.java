package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.models.CommentModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.CommentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentServiceImpl commentService;

    @Autowired
    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/search/all")
    public ResponseEntity<CollectionModel<EntityModel<CommentModel>>> getAllComments() {
        List<EntityModel<CommentModel>> comments = commentService.getAllComments()
                .stream().map(this::getCommentEntityModel).toList();
        return new ResponseEntity<>(CollectionModel.of(comments).add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CommentController.class).getAllComments()).withSelfRel())
                , HttpStatus.OK);
    }
    @GetMapping("/search/{commentId}")
    public ResponseEntity<EntityModel<CommentModel>> getCommentById(@PathVariable Long commentId) {
        if(commentService.getComment(commentId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CommentModel commentModel = commentService.getComment(commentId).get();
        return ResponseEntity.ok(getCommentEntityModel(commentModel));
    }

    @PostMapping("/new")
    public ResponseEntity<EntityModel<CommentModel>> createComment(@RequestBody CommentModel commentModel) {
        if(!commentService.validateNewComment(commentModel)){
            return ResponseEntity.badRequest().build();
        }
        CommentModel newComment = commentService.createComment(commentModel);
        URI location = URI.create("/api/comments/search/"+newComment.getId());
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CommentController.class).getCommentById(newComment.getId())).withSelfRel();
        return ResponseEntity.created(location).body(EntityModel.of(newComment).add(selfLink));
    }
    @PutMapping("/update/{commentId}")
    public ResponseEntity updateComment(@PathVariable Long commentId, @RequestBody CommentModel commentModel) {
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
    public ResponseEntity deleteComment(@PathVariable Long commentId) {
        if(commentService.getComment(commentId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    private Link getUserLink(CommentModel commentModel){
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserApiController.class).getUserById(commentModel.getUserModel().getId())).withRel("User: " + commentModel.getUserModel().getUsername());
    }

    private Link getGameLink(CommentModel commentModel){
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).getGameById(commentModel.getGamesModel().getId())).withRel("Game: " + commentModel.getGamesModel().getTitle());
    }

    private EntityModel<CommentModel> getCommentEntityModel(CommentModel commentModel) {
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CommentController.class).getCommentById(commentModel.getId())).withSelfRel();
        Link relink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CommentController.class).getAllComments()).withRel("All Comments");
        return EntityModel.of(commentModel, selfLink, relink, getUserLink(commentModel), getGameLink(commentModel));
    }


}
