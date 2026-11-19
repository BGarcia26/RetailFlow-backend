package cl.rinconsur.retailflow.api.repository;

import cl.rinconsur.retailflow.api.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Capa Repository: acceso a la tabla "turnos".
 * JpaRepository ya trae save, findById, findAll, deleteById, existsById...
 * Spring genera la implementación en tiempo de ejecución.
 */
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    // Spring deduce el SQL desde el nombre del método: ORDER BY id DESC
    List<Turno> findAllByOrderByIdDesc();
}
