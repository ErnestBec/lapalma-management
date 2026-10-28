package com.lapalma.management.shared.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice 
public class GlobalExceptionHandler {
    //MethodArgumentNotValidException -> es una excepción que Spring MVC puede lanzar cuando @Valid detecta errores en el @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception){
        Map<String, String> errors = new LinkedHashMap<>();

        //Obtiene los errores de cada campo
        exception.getBindingResult()
            .getFieldErrors()
            .forEach(error -> 
                //Construyen un objeto con sus mensajes de error
                //pitIfAbsent() -> conserva el primer mensaje por campo. Si necesitamos mas mensajes se cambia por una llista
                errors.putIfAbsent(error.getField(), 
                error.getDefaultMessage()));
        
        Map<String, Object> response = Map.of(
            "status", 400,
            "message", "Error de validación",
            "errors", errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
