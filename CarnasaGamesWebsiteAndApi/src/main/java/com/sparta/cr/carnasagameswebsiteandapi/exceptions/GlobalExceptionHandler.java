package com.sparta.cr.carnasagameswebsiteandapi.exceptions;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.*;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.commentexceptions.*;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions.*;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.scoreexceptions.*;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("CONFLICT", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(InvalidUsernameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUsernameNotValidException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(InvalidEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidEmailException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("CONFLICT", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CantChangeUsernameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleCantChangeUsernameException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidRoleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidRoleException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(GameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleGameAlreadyExistsException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("CONFLICT", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(InvalidGenreException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidGenreException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidTitleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidTitleException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleNoUserException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidGameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleNoGameException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ModelAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleCommentAlreadyExistsException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("CONFLICT", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(ModelNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleCommentNotFoundException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(CommentMustHaveTextException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleCommentMustHaveTextException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidDateException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ScoreOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleScoreOutOfBoundsException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponse("FORBIDDEN", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.FORBIDDEN);
    }
    private record ErrorResponse(Object errorDetails, String errorCode, String url){}
}
