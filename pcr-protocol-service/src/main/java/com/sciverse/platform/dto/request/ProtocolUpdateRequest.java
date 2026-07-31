package com.sciverse.platform.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sciverse.platform.domain.ProtocolStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProtocolUpdateRequest {

    private String name;
    private String description;

    @Valid
    @NotEmpty(message = "At least one step is required")
    private List<ProtocolStepRequest> steps;

    @Min(value = 1, message = "Cycle count must be at least 1")
    @Max(value = 60, message = "Cycle count must be at most 60")
    private Integer cycleCount;

    @DecimalMin(value = "0.1", message = "Ramp rate must be at least 0.1°C/s")
    @DecimalMax(value = "6.0", message = "Ramp rate must be at most 6.0°C/s")
    private BigDecimal rampRate;

    private Integer version;
    private ProtocolStatus status;

    public ProtocolUpdateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<ProtocolStepRequest> getSteps() { return steps; }
    public void setSteps(List<ProtocolStepRequest> steps) { this.steps = steps; }

    public Integer getCycleCount() { return cycleCount; }
    public void setCycleCount(Integer cycleCount) { this.cycleCount = cycleCount; }

    public BigDecimal getRampRate() { return rampRate; }
    public void setRampRate(BigDecimal rampRate) { this.rampRate = rampRate; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public ProtocolStatus getStatus() { return status; }
    public void setStatus(ProtocolStatus status) { this.status = status; }
}
