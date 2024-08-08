package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelAlreadyExistsException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.commentexceptions.CommentMustHaveTextException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidGameException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidUserException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidDateException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UserNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.CommentModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.CommentRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.CommentServiceable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommentServiceImpl implements CommentServiceable {

    private final CommentRepository commentRepository;
    private final GameServiceImpl gameService;
    private final UserServiceImpl userService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, GameServiceImpl gameService, UserServiceImpl userService) {
        this.commentRepository = commentRepository;
        this.gameService = gameService;
        this.userService = userService;
    }

    @Override
    public CommentModel createComment(CommentModel comment) {
        if(validateNewComment(comment)){
            comment.setIsUpdated(false);
            comment.setDate(LocalDate.now());
            comment.setUserModel(userService.getUser(comment.getUserModel().getId()).get());
            comment.setGamesModel(gameService.getGame(comment.getGamesModel().getId()).get());
            CommentModel cleanedComment = censorBadText(comment);
            return commentRepository.save(cleanedComment);
        }
        return null;
    }

    @Override
    public CommentModel updateComment(CommentModel comment) {
        if(validateExistingComment(comment)){
            comment.setDate(LocalDate.now());
            comment.setUserModel(userService.getUser(comment.getUserModel().getId()).get());
            comment.setGamesModel(gameService.getGame(comment.getGamesModel().getId()).get());
            comment.setIsUpdated(true);
            CommentModel cleanedComment = censorBadText(comment);
            return commentRepository.save(cleanedComment);
        }
        return null;
    }

    @Override
    public CommentModel deleteComment(Long commentId) {
        if(getComment(commentId).isPresent()){
            CommentModel comment = getComment(commentId).get();
            commentRepository.delete(getComment(commentId).get());
            return comment;
        }
        return null;
    }

    @Override
    public Optional<CommentModel> getComment(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public List<CommentModel> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    @Transactional
    public List<CommentModel> getCommentsByUser(Long userId) {
        return getAllComments()
                .stream()
                .filter(commentModel -> commentModel.getUserModel().getId().equals(userId))
                .toList();
    }

    @Override
    @Transactional
    public List<CommentModel> getCommentsByGame(Long gameId) {
        return getAllComments()
                .stream()
                .filter(commentModel -> commentModel.getGamesModel().getId().equals(gameId))
                .toList();
    }

    @Override
    @Transactional
    public List<CommentModel> getCommentsByGameAndUser(Long gameId, Long userId) {
        return getCommentsByGame(gameId)
                .stream()
                .filter(commentModel -> commentModel.getUserModel().getId().equals(userId))
                .toList();
    }

    @Override
    public List<CommentModel> getCommentsByDate(String startDate, String endDate) {

        LocalDate startDateDate;
        LocalDate endDateDate;

        if(validateDateFormat(startDate) && validateDateFormat(endDate)){
            startDateDate = LocalDate.parse(startDate);
            endDateDate = LocalDate.parse(endDate);
        }
        else {
            throw new InvalidDateException("Dates must be a valid date and in format yyyy-MM-dd!");
        }
        if(startDateDate.isAfter(endDateDate)){
            throw new InvalidDateException("Start date must be before end date!");
        }
        if(startDateDate.isAfter(LocalDate.now())){
            throw new InvalidDateException("Start date cannot be in the future!");
        }
        return getAllComments().stream().filter(commentModel -> isInDateRange(startDateDate,endDateDate,commentModel.getDate())).toList();
    }

    @Override
    public List<CommentModel> getCommentsFromToday(LocalDate today) {
        return getAllComments().stream().filter(commentModel -> commentModel.getDate().equals(today)).toList();
    }

    private boolean isInDateRange(LocalDate start, LocalDate end, LocalDate commentDate) {
        return start.isBefore(commentDate) && end.isAfter(commentDate);
    }

    public boolean validateNewComment(CommentModel comment) {

        if(getComment(comment.getId()).isPresent()){
            throw new ModelAlreadyExistsException("Cannot create new comment with ID: " + comment.getId() + " already exists");
        }
        if(userService.getUser(comment.getUserModel().getId()).isEmpty()){
            throw new InvalidUserException("Cannot create comment as user with ID: " + comment.getUserModel().getId()+" does not exist." );
        }
        if(gameService.getGame(comment.getGamesModel().getId()).isEmpty()){
            throw new InvalidGameException("Cannot create comment as game with ID: " + comment.getGamesModel().getId() + " does not exist." );
        }
        if(comment.getCommentText().isEmpty()){
            throw new CommentMustHaveTextException("Cannot create comment as comment must contain text");
        }
        return true;
    }

    public boolean validateExistingComment(CommentModel comment) {
        if(getComment(comment.getId()).isEmpty()){
            throw new ModelNotFoundException("Cannot update comment as ID: " + comment.getId() + " does not exist." );
        }
        CommentModel beforeUpdate = getComment(comment.getId()).get();
        if(!beforeUpdate.getUserModel().getId().equals(comment.getUserModel().getId())){
            throw new InvalidUserException("Cannot update comment as new user ID detected");
        }
        if(!beforeUpdate.getGamesModel().getId().equals(comment.getGamesModel().getId())){
            throw new InvalidGameException("Cannot update comment as new game ID detected");
        }
        if(comment.getCommentText().isEmpty()){
            throw new CommentMustHaveTextException("Cannot update comment as comment must contain text, have you tried deleting?");
        }
        return true;
    }

    public CommentModel censorBadText(CommentModel comment) {

        String[] censoredText = {
                "fuck","shit","cunt","arse","ass","shite","sh1t","5hit","5h1t","ar5e","a55","a5s","as5","wank", "fuck", "shit", "bitch", "asshole", "bastard", "crap", "dickhead",
                "motherfucker", "piss", "twat", "wanker", "bollocks", "bugger", "nigger", "chink", "faggot", "queer", "kike", "wetback", "gook",
                "raghead", "tarbaby", "tranny", "heeb", "slant", "gypsy", "porn", "dick", "pussy", "blowjob", "anal", "cum", "tits",
                "nipple", "vagina", "penis", "whore", "slut", "fisting", "handjob", "masturbate", "orgasm", "boner", "buttfuck", "clit", "dildo", "ejaculate",
                "hentai",  "stripper", "buttplug", "fellatio", "cunnilingus", "bukkake", "pegging", "rimjob" //can add more if needed but I think this is enough, thanks chatGpt for saving me writing out those...
        };

        String patternString = "(" + String.join("|", censoredText) + ")";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(comment.getCommentText());
        StringBuilder censoredComment = new StringBuilder();

        while (matcher.find()) {
            String replacement = "*".repeat(matcher.group().length());
            matcher.appendReplacement(censoredComment, replacement);
        }
        matcher.appendTail(censoredComment);
        comment.setCommentText(censoredComment.toString());
        return comment;
    }

    private boolean validateDateIsReal(String Date) {
        try{
            LocalDate parseDate = LocalDate.parse(Date, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        }
        catch (DateTimeParseException e){
            return false;
        }
    }

    private boolean validateDateFormat(String date) {
        String DatePattern = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
        Pattern pattern = Pattern.compile(DatePattern);
        Matcher matcher = pattern.matcher(date);
        if(matcher.matches()){
            return validateDateIsReal(date);
        }
        return false;
    }
}
