package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestAlertException(BadRequestAlertException ex) {
        Map<String, Object> body = new HashMap<>();

        switch (ex.getErrorKey()) {
            case "timeoverlap":
                body.put("message", "The selected time is already booked. Please choose another slot.");
                break;
            case "roomnotfound":
                body.put("message", "The selected meeting room does not exist.");
                break;
            case "employeenotfound":
                body.put("message", "Employee not found for current user.");
                break;
            default:
                body.put("message", "Invalid request.");
        }

        body.put("entity", ex.getEntityName());
        body.put("errorKey", ex.getErrorKey());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
