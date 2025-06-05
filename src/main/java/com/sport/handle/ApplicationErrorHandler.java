package com.sport.handle;

import com.sport.dto.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.sport.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ApplicationErrorHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ErrorDTO> resourceNotFoundException(HttpServletRequest request, ResourceNotFoundException ex) {
        log.error("{} - {}", "ResourceNotFoundException", ex.getMessage());
        ErrorDTO errorDTO = new ErrorDTO(ex.getMessage(), HttpStatus.NOT_FOUND.value());
       return new ResponseEntity(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorDTO> handleException(HttpServletRequest request, Exception ex) {
        log.error("{} - {}", "Not controlled exception", ex.getMessage());
        ErrorDTO errorDTO = new ErrorDTO("Sorry, we are working about it", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}