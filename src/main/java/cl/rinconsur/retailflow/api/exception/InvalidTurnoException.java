package cl.rinconsur.retailflow.api.exception;

/** Reglas de negocio que no se pueden expresar con anotaciones (ej: más compradores que personas). Se traduce a 400. */
public class InvalidTurnoException extends RuntimeException {

    public InvalidTurnoException(String message) {
        super(message);
    }
}
