package com.sciverse.platform.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sciverse.platform.domain.ProtocolStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProtocolCreateRequest {

    @NotBlank(message = "Protocol name is required")
    private String name;

    private String description;

    @NotEmpty(message = "At least one step is required")
    @Valid
    private List<ProtocolStepRequest> steps;

    @NotNull(message = "Cycle count is required")
    @Min(value = 1, message = "Cycle count must be at least 1")
    @Max(value = 60, message = "Cycle count must be at most 60")
    private Integer cycleCount;

    @NotNull(message = "Ramp rate is required")
    @DecimalMin(value = "0.1", message = "Ramp rate must be at least 0.1°C/s")
    @DecimalMax(value = "6.0", message = "Ramp rate must be at most 6.0°C/s")
    private BigDecimal rampRate;
    private ProtocolStatus status;

    public ProtocolCreateRequest() {}

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

    public ProtocolStatus getStatus() { return status; }
    public void setStatus(ProtocolStatus status) { this.status = status; }
}
