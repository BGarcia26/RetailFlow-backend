package cl.rinconsur.retailflow.api.service;

import cl.rinconsur.retailflow.api.dto.TurnoRequest;
import cl.rinconsur.retailflow.api.dto.TurnoResponse;
import cl.rinconsur.retailflow.api.exception.InvalidTurnoException;
import cl.rinconsur.retailflow.api.exception.TurnoNotFoundException;
import cl.rinconsur.retailflow.api.model.Turno;
import cl.rinconsur.retailflow.api.repository.TurnoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Prueba la lógica del Service sin base de datos: el Repository es un mock de Mockito
 * y cada test define qué responde y verifica qué se le pidió.
 */
@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    @Mock
    private TurnoRepository repository;

    @InjectMocks
    private TurnoService service;

    private static TurnoRequest request(int total, int buyers, String notes) {
        return new TurnoRequest("19/11/2026 (Jueves)", "Tarde (16:30 hrs)", total, buyers,
                1, 2, 3, 0, 1, 1, 1, "Acceso 1: 6 pers.", notes);
    }

    // save() devuelve el mismo objeto con id, como haría la base de datos
    private void saveAssignsId(long id) {
        when(repository.save(any(Turno.class))).thenAnswer(inv -> {
            Turno t = inv.getArgument(0);
            t.setId(id);
            return t;
        });
    }

    @Test
    @DisplayName("create calcula el porcentaje de tránsito y guarda el turno")
    void create_computesTransitPercentage() {
        saveAssignsId(1L);

        TurnoResponse result = service.create(request(6, 2, "  Lluvia  "));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.transitOnlyPercentage()).isEqualTo(67); // 4 de 6 = 66,7 % -> 67
        assertThat(result.notes()).isEqualTo("Lluvia");
        verify(repository).save(any(Turno.class));
    }

    @Test
    @DisplayName("create rechaza más compradores que personas y no guarda nada")
    void create_rejectsMoreBuyersThanTotal() {
        assertThatThrownBy(() -> service.create(request(3, 5, null)))
                .isInstanceOf(InvalidTurnoException.class)
                .hasMessageContaining("compradores");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("una observación en blanco se guarda como null")
    void create_blankNotesBecomeNull() {
        saveAssignsId(2L);
        ArgumentCaptor<Turno> captor = ArgumentCaptor.forClass(Turno.class);

        service.create(request(6, 2, "   "));

        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getNotes()).isNull();
    }

    @Test
    @DisplayName("findById lanza 404 si el turno no existe")
    void findById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(TurnoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findAll devuelve los turnos en el orden que entrega el Repository")
    void findAll_mapsEntities() {
        Turno a = new Turno();
        a.setId(2L);
        a.setDateLabel("b");
        Turno b = new Turno();
        b.setId(1L);
        b.setDateLabel("a");
        when(repository.findAllByOrderByIdDesc()).thenReturn(List.of(a, b));

        assertThat(service.findAll()).extracting(TurnoResponse::id).containsExactly(2L, 1L);
    }

    @Test
    @DisplayName("update modifica el turno existente y recalcula el porcentaje")
    void update_existing() {
        Turno existing = new Turno();
        existing.setId(5L);
        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        TurnoResponse result = service.update(5L, request(10, 10, "Todo bien"));

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.transitOnlyPercentage()).isZero();
        assertThat(result.notes()).isEqualTo("Todo bien");
    }

    @Test
    @DisplayName("delete de un id inexistente lanza 404 y no borra")
    void delete_notFound() {
        when(repository.existsById(7L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(7L)).isInstanceOf(TurnoNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete de un id existente lo borra")
    void delete_existing() {
        when(repository.existsById(7L)).thenReturn(true);

        service.delete(7L);

        verify(repository).deleteById(7L);
    }

    @Test
    @DisplayName("el porcentaje con flujo 0 es 0 (sin dividir por cero)")
    void transitPercentage_zeroTotal() {
        assertThat(TurnoService.transitPercentage(0, 0)).isZero();
    }
}
