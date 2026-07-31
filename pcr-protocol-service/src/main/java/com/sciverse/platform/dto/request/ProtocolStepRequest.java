package com.sciverse.platform.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProtocolStepRequest {

    @NotBlank(message = "Step name is required")
    private String name;

    @DecimalMin(value = "4.0", message = "Temperature must be at least 4.0°C")
    @DecimalMax(value = "99.0", message = "Temperature must be at most 99.0°C")
    private BigDecimal targetTemperatureCelsius;

    @Min(value = 1, message = "Hold time must be at least 1 second")
    @Max(value = 3600, message = "Hold time must be at most 3600 seconds")
    private Integer holdTimeSeconds;

    public ProtocolStepRequest() {}

    public ProtocolStepRequest(String name, BigDecimal targetTemperatureCelsius, Integer holdTimeSeconds) {
        this.name = name;
        this.targetTemperatureCelsius = targetTemperatureCelsius;
        this.holdTimeSeconds = holdTimeSeconds;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getTargetTemperatureCelsius() { return targetTemperatureCelsius; }
    public void setTargetTemperatureCelsius(BigDecimal targetTemperatureCelsius) {
        this.targetTemperatureCelsius = targetTemperatureCelsius;
    }

    public Integer getHoldTimeSeconds() { return holdTimeSeconds; }
    public void setHoldTimeSeconds(Integer holdTimeSeconds) { this.holdTimeSeconds = holdTimeSeconds; }
}
