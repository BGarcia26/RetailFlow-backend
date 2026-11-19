package cl.rinconsur.retailflow.api.dto;

import cl.rinconsur.retailflow.api.model.Turno;

import java.time.Instant;

/** JSON que devuelve la API. Se arma desde la entidad para no exponer la clase de JPA directamente. */
public record TurnoResponse(
        Long id,
        String dateLabel,
        String shiftTime,
        int totalFlow,
        int buyersCount,
        int transitOnlyPercentage,
        int childCount,
        int youngCount,
        int adultCount,
        int seniorCount,
        int soloCount,
        int coupleCount,
        int groupCount,
        String accessSummary,
        String notes,
        Instant createdAt
) {
    public static TurnoResponse from(Turno t) {
        return new TurnoResponse(
                t.getId(), t.getDateLabel(), t.getShiftTime(),
                t.getTotalFlow(), t.getBuyersCount(), t.getTransitOnlyPercentage(),
                t.getChildCount(), t.getYoungCount(), t.getAdultCount(), t.getSeniorCount(),
                t.getSoloCount(), t.getCoupleCount(), t.getGroupCount(),
                t.getAccessSummary(), t.getNotes(), t.getCreatedAt()
        );
    }
}
