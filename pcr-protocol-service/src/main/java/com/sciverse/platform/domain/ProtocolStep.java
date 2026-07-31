package com.sciverse.platform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class ProtocolStep {

    @Column(name = "step_name", nullable = false)
    private String name;

    @Column(name = "target_temperature_celsius", nullable = false)
    private BigDecimal targetTemperatureCelsius;

    @Column(name = "hold_time_seconds", nullable = false)
    private Integer holdTimeSeconds;

    public ProtocolStep() {}

    public ProtocolStep(String name, BigDecimal targetTemperatureCelsius, Integer holdTimeSeconds) {
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
