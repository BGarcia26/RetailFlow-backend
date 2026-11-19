package cl.rinconsur.retailflow.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Responde "ok" sin tocar la base de datos.
 * Render lo usa para saber si el servicio está vivo, y sirve para despertarlo desde el navegador.
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
