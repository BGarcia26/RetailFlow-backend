package cl.rinconsur.retailflow.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convierte las excepciones en respuestas HTTP con un JSON simple { "error": "..." }.
 * Sin esto, Spring respondería 500 o un HTML de error que la app no sabría leer.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TurnoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(TurnoNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(InvalidTurnoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalid(InvalidTurnoException e) {
        return Map.of("error", e.getMessage());
    }

    // Errores de @Valid: se juntan todos los campos inválidos en un solo mensaje
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validation(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Map.of("error", detail);
    }
}
