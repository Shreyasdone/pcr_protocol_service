package com.sciverse.platform.dto.response;

import com.sciverse.platform.domain.ProtocolStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ProtocolResponse {

    private UUID id;
    private String name;
    private String description;
    private List<ProtocolStepResponse> steps;
    private Integer cycleCount;
    private BigDecimal rampRate;
    private ProtocolStatus status;
    private Integer version;
    private Instant createdAt;
    private Instant updatedAt;

    public ProtocolResponse() {}

    public ProtocolResponse(UUID id, String name, String description, List<ProtocolStepResponse> steps,
                            Integer cycleCount, BigDecimal rampRate, ProtocolStatus status,
                            Integer version, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.steps = steps;
        this.cycleCount = cycleCount;
        this.rampRate = rampRate;
        this.status = status;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<ProtocolStepResponse> getSteps() { return steps; }
    public Integer getCycleCount() { return cycleCount; }
    public BigDecimal getRampRate() { return rampRate; }
    public ProtocolStatus getStatus() { return status; }
    public Integer getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
