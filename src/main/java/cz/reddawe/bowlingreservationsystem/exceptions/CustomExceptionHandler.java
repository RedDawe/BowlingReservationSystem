package cz.reddawe.bowlingreservationsystem.exceptions;

import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.BadRequestException;
import cz.reddawe.bowlingreservationsystem.exceptions.forbidden.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException e) {

        ExceptionResponse payload = new ExceptionResponse(
                e.getError(), e.getReason(), ZonedDateTime.now());

        return ResponseEntity.badRequest().body(payload);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleForbiddenException(ForbiddenException e) {

        ExceptionResponse payload = new ExceptionResponse(
                "This action is forbidden", e.getMessage(), ZonedDateTime.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(payload);
    }
}
