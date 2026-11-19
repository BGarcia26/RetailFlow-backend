package cl.rinconsur.retailflow.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Tabla "turnos": un turno de muestreo cerrado en la app.
 * No se escribe SQL a mano: Hibernate crea la tabla a partir de esta clase (ddl-auto=update).
 */
@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String dateLabel;

    @Column(nullable = false)
    private String shiftTime;

    private int totalFlow;
    private int buyersCount;
    // Lo calcula el Service, no el cliente: así nunca queda inconsistente con los totales
    private int transitOnlyPercentage;

    private int childCount;
    private int youngCount;
    private int adultCount;
    private int seniorCount;

    private int soloCount;
    private int coupleCount;
    private int groupCount;

    @Column(length = 500)
    private String accessSummary;

    // Lo único que se edita con PUT desde la app: una observación del supervisor
    @Column(length = 500)
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Se fija una sola vez, justo antes del primer INSERT
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDateLabel() { return dateLabel; }
    public void setDateLabel(String dateLabel) { this.dateLabel = dateLabel; }

    public String getShiftTime() { return shiftTime; }
    public void setShiftTime(String shiftTime) { this.shiftTime = shiftTime; }

    public int getTotalFlow() { return totalFlow; }
    public void setTotalFlow(int totalFlow) { this.totalFlow = totalFlow; }

    public int getBuyersCount() { return buyersCount; }
    public void setBuyersCount(int buyersCount) { this.buyersCount = buyersCount; }

    public int getTransitOnlyPercentage() { return transitOnlyPercentage; }
    public void setTransitOnlyPercentage(int transitOnlyPercentage) { this.transitOnlyPercentage = transitOnlyPercentage; }

    public int getChildCount() { return childCount; }
    public void setChildCount(int childCount) { this.childCount = childCount; }

    public int getYoungCount() { return youngCount; }
    public void setYoungCount(int youngCount) { this.youngCount = youngCount; }

    public int getAdultCount() { return adultCount; }
    public void setAdultCount(int adultCount) { this.adultCount = adultCount; }

    public int getSeniorCount() { return seniorCount; }
    public void setSeniorCount(int seniorCount) { this.seniorCount = seniorCount; }

    public int getSoloCount() { return soloCount; }
    public void setSoloCount(int soloCount) { this.soloCount = soloCount; }

    public int getCoupleCount() { return coupleCount; }
    public void setCoupleCount(int coupleCount) { this.coupleCount = coupleCount; }

    public int getGroupCount() { return groupCount; }
    public void setGroupCount(int groupCount) { this.groupCount = groupCount; }

    public String getAccessSummary() { return accessSummary; }
    public void setAccessSummary(String accessSummary) { this.accessSummary = accessSummary; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
