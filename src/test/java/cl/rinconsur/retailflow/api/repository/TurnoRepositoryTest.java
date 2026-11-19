package cl.rinconsur.retailflow.api.repository;

import cl.rinconsur.retailflow.api.model.Turno;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba el Repository contra una base H2 real en memoria:
 * confirma que Hibernate crea la tabla y que las consultas funcionan.
 */
@DataJpaTest
class TurnoRepositoryTest {

    @Autowired
    private TurnoRepository repository;

    private static Turno turno(String date) {
        Turno t = new Turno();
        t.setDateLabel(date);
        t.setShiftTime("Mañana (10:00 hrs)");
        t.setTotalFlow(5);
        return t;
    }

    @Test
    @DisplayName("save asigna id y fecha de creación")
    void save_assignsIdAndCreatedAt() {
        Turno saved = repository.save(turno("19/11/2026 (Jueves)"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("findAllByOrderByIdDesc devuelve primero el más nuevo")
    void findAll_newestFirst() {
        Turno first = repository.save(turno("primero"));
        Turno second = repository.save(turno("segundo"));

        assertThat(repository.findAllByOrderByIdDesc())
                .extracting(Turno::getId)
                .containsExactly(second.getId(), first.getId());
    }

    @Test
    @DisplayName("deleteById elimina el turno")
    void delete_removes() {
        Turno saved = repository.save(turno("borrar"));

        repository.deleteById(saved.getId());

        assertThat(repository.existsById(saved.getId())).isFalse();
    }
}
