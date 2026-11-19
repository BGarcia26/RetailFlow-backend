package cl.rinconsur.retailflow.api.controller;

import cl.rinconsur.retailflow.api.dto.TurnoRequest;
import cl.rinconsur.retailflow.api.dto.TurnoResponse;
import cl.rinconsur.retailflow.api.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Capa Controller: recibe las peticiones HTTP, valida el JSON y delega al Service.
 * Cada método corresponde a un método HTTP del CRUD.
 */
@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TurnoResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TurnoResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    // 201 Created: es lo correcto en REST cuando se crea un recurso
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TurnoResponse create(@Valid @RequestBody TurnoRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public TurnoResponse update(@PathVariable Long id, @Valid @RequestBody TurnoRequest request) {
        return service.update(id, request);
    }

    // 204 No Content: se borró y no hay nada que devolver
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
