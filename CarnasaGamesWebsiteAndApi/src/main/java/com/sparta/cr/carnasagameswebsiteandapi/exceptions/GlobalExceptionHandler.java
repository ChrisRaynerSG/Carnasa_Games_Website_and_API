package com.sparta.cr.carnasagameswebsiteandapi.exceptions;

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

    private record ErrorResponse(Object errorDetails, String errorCode, String url){}
}
