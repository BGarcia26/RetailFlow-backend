package cl.rinconsur.retailflow.api.service;

import cl.rinconsur.retailflow.api.dto.TurnoRequest;
import cl.rinconsur.retailflow.api.dto.TurnoResponse;
import cl.rinconsur.retailflow.api.exception.InvalidTurnoException;
import cl.rinconsur.retailflow.api.exception.TurnoNotFoundException;
import cl.rinconsur.retailflow.api.model.Turno;
import cl.rinconsur.retailflow.api.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Capa Service: reglas de negocio. No sabe nada de HTTP (eso es del Controller)
 * ni de SQL (eso es del Repository), por eso se puede probar con un Repository simulado.
 */
@Service
public class TurnoService {

    private final TurnoRepository repository;

    // Inyección por constructor: Spring entrega el Repository y en los tests se pasa un mock
    public TurnoService(TurnoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TurnoResponse> findAll() {
        return repository.findAllByOrderByIdDesc().stream().map(TurnoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TurnoResponse findById(Long id) {
        return TurnoResponse.from(getOrThrow(id));
    }

    @Transactional
    public TurnoResponse create(TurnoRequest request) {
        Turno turno = new Turno();
        apply(request, turno);
        return TurnoResponse.from(repository.save(turno));
    }

    @Transactional
    public TurnoResponse update(Long id, TurnoRequest request) {
        Turno turno = getOrThrow(id);
        apply(request, turno);
        return TurnoResponse.from(repository.save(turno));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TurnoNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /** Porcentaje de personas que pasaron sin comprar, redondeado (igual que en la app). */
    static int transitPercentage(int totalFlow, int buyersCount) {
        if (totalFlow <= 0) return 0;
        return Math.round((totalFlow - buyersCount) * 100f / totalFlow);
    }

    private Turno getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new TurnoNotFoundException(id));
    }

    // Copia los datos del request a la entidad y valida lo que depende de dos campos a la vez
    private void apply(TurnoRequest r, Turno t) {
        if (r.buyersCount() > r.totalFlow()) {
            throw new InvalidTurnoException("Los compradores no pueden superar el flujo total");
        }
        t.setDateLabel(r.dateLabel().trim());
        t.setShiftTime(r.shiftTime().trim());
        t.setTotalFlow(r.totalFlow());
        t.setBuyersCount(r.buyersCount());
        t.setTransitOnlyPercentage(transitPercentage(r.totalFlow(), r.buyersCount()));
        t.setChildCount(r.childCount());
        t.setYoungCount(r.youngCount());
        t.setAdultCount(r.adultCount());
        t.setSeniorCount(r.seniorCount());
        t.setSoloCount(r.soloCount());
        t.setCoupleCount(r.coupleCount());
        t.setGroupCount(r.groupCount());
        t.setAccessSummary(r.accessSummary());
        // Una observación vacía se guarda como null para no tener "" y null significando lo mismo
        t.setNotes(r.notes() == null || r.notes().isBlank() ? null : r.notes().trim());
    }
}
