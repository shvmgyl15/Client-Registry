package in.org.projecteka.clientregistry.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }
}