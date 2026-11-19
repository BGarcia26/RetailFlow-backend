package cl.rinconsur.retailflow.api.exception;

/** Se lanza cuando se pide, edita o borra un id que no existe. El handler la traduce a 404. */
public class TurnoNotFoundException extends RuntimeException {

    public TurnoNotFoundException(Long id) {
        super("No existe el turno " + id);
    }
}
