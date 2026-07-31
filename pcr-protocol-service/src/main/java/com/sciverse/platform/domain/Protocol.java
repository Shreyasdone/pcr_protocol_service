package com.sciverse.platform.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "protocols")
public class Protocol {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "protocol_steps", joinColumns = @JoinColumn(name = "protocol_id"))
    @OrderColumn(name = "step_order")
    private List<ProtocolStep> steps = new ArrayList<>();

    @Column(nullable = false)
    private Integer cycleCount;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal rampRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProtocolStatus status = ProtocolStatus.ACTIVE;

    @Version
    @Column(nullable = false)
    private Integer version;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public Protocol() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<ProtocolStep> getSteps() { return steps; }
    public void setSteps(List<ProtocolStep> steps) { this.steps = steps; }

    public Integer getCycleCount() { return cycleCount; }
    public void setCycleCount(Integer cycleCount) { this.cycleCount = cycleCount; }

    public BigDecimal getRampRate() { return rampRate; }
    public void setRampRate(BigDecimal rampRate) { this.rampRate = rampRate; }

    public ProtocolStatus getStatus() { return status; }
    public void setStatus(ProtocolStatus status) { this.status = status; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
