package cz.reddawe.bowlingreservationsystem.exceptions;

import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleResourceException(
            BadRequestException e) {

        ExceptionResponse payload = new ExceptionResponse(
                e.getError(), e.getReason(), ZonedDateTime.now());

        return ResponseEntity.badRequest().body(payload);
    }
}
