package cl.rinconsur.retailflow.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * JSON que envía la app en POST y PUT.
 * Es distinto de la entidad a propósito: el cliente no decide el id, la fecha de creación
 * ni el porcentaje de tránsito, que calcula el servidor.
 * Las anotaciones las revisa @Valid en el Controller; si algo falla responde 400.
 */
public record TurnoRequest(
        @NotBlank(message = "La fecha es obligatoria") String dateLabel,
        @NotBlank(message = "El horario es obligatorio") String shiftTime,
        @Min(value = 0, message = "No puede ser negativo") int totalFlow,
        @Min(value = 0, message = "No puede ser negativo") int buyersCount,
        @Min(0) int childCount,
        @Min(0) int youngCount,
        @Min(0) int adultCount,
        @Min(0) int seniorCount,
        @Min(0) int soloCount,
        @Min(0) int coupleCount,
        @Min(0) int groupCount,
        @Size(max = 500) String accessSummary,
        @Size(max = 500, message = "Máximo 500 caracteres") String notes
) {
}
