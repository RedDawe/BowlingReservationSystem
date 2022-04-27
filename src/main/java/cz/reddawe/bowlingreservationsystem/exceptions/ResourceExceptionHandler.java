package cz.reddawe.bowlingreservationsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(value = ResourceException.class)
    public ResponseEntity<ResourceExceptionResponse> handleResourceException(
            ResourceException e) {

        ResourceExceptionResponse payload = new ResourceExceptionResponse(
                e.getReason(), e.getResource(), ZonedDateTime.now());

        return ResponseEntity.badRequest().body(payload);
    }
}
