package cl.rinconsur.retailflow.api.controller;

import cl.rinconsur.retailflow.api.dto.TurnoRequest;
import cl.rinconsur.retailflow.api.dto.TurnoResponse;
import cl.rinconsur.retailflow.api.exception.TurnoNotFoundException;
import cl.rinconsur.retailflow.api.service.TurnoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba solo la capa web: rutas, métodos HTTP, códigos de estado y JSON.
 * MockMvc simula las peticiones sin levantar un servidor real y el Service es un mock.
 */
@WebMvcTest(TurnoController.class)
class TurnoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TurnoService service;

    private static final String VALID_JSON = """
            {"dateLabel":"19/11/2026 (Jueves)","shiftTime":"Tarde (16:30 hrs)",
             "totalFlow":6,"buyersCount":2,"childCount":1,"youngCount":2,"adultCount":3,
             "seniorCount":0,"soloCount":1,"coupleCount":1,"groupCount":1,
             "accessSummary":"Acceso 1: 6 pers.","notes":"Lluvia"}
            """;

    private static TurnoResponse sample(long id) {
        return new TurnoResponse(id, "19/11/2026 (Jueves)", "Tarde (16:30 hrs)", 6, 2, 67,
                1, 2, 3, 0, 1, 1, 1, "Acceso 1: 6 pers.", "Lluvia", Instant.parse("2026-11-19T19:30:00Z"));
    }

    @Test
    @DisplayName("GET /api/turnos devuelve la lista en JSON")
    void getAll() throws Exception {
        when(service.findAll()).thenReturn(List.of(sample(2), sample(1)));

        mvc.perform(get("/api/turnos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].transitOnlyPercentage").value(67));
    }

    @Test
    @DisplayName("GET /api/turnos/{id} inexistente responde 404 con mensaje")
    void getById_notFound() throws Exception {
        when(service.findById(9L)).thenThrow(new TurnoNotFoundException(9L));

        mvc.perform(get("/api/turnos/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe el turno 9"));
    }

    @Test
    @DisplayName("POST /api/turnos crea y responde 201")
    void create() throws Exception {
        when(service.create(any(TurnoRequest.class))).thenReturn(sample(1));

        mvc.perform(post("/api/turnos").contentType(MediaType.APPLICATION_JSON).content(VALID_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.notes").value("Lluvia"));
    }

    @Test
    @DisplayName("POST con datos inválidos responde 400 y no llama al Service")
    void create_invalid() throws Exception {
        String invalid = VALID_JSON.replace("\"totalFlow\":6", "\"totalFlow\":-1")
                .replace("\"dateLabel\":\"19/11/2026 (Jueves)\"", "\"dateLabel\":\"\"");

        mvc.perform(post("/api/turnos").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
        verify(service, never()).create(any());
    }

    @Test
    @DisplayName("PUT /api/turnos/{id} actualiza y responde 200")
    void update() throws Exception {
        when(service.update(eq(1L), any(TurnoRequest.class))).thenReturn(sample(1));

        mvc.perform(put("/api/turnos/1").contentType(MediaType.APPLICATION_JSON).content(VALID_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/turnos/{id} responde 204")
    void deleteExisting() throws Exception {
        mvc.perform(delete("/api/turnos/1")).andExpect(status().isNoContent());
        verify(service).delete(1L);
    }

    @Test
    @DisplayName("DELETE de un id inexistente responde 404")
    void deleteMissing() throws Exception {
        doThrow(new TurnoNotFoundException(3L)).when(service).delete(3L);

        mvc.perform(delete("/api/turnos/3")).andExpect(status().isNotFound());
    }
}
