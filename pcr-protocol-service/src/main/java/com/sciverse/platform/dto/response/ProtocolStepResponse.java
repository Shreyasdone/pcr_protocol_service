package com.sciverse.platform.dto.response;

import java.math.BigDecimal;

public class ProtocolStepResponse {

    private String name;
    private BigDecimal targetTemperatureCelsius;
    private Integer holdTimeSeconds;

    public ProtocolStepResponse() {}

    public ProtocolStepResponse(String name, BigDecimal targetTemperatureCelsius, Integer holdTimeSeconds) {
        this.name = name;
        this.targetTemperatureCelsius = targetTemperatureCelsius;
        this.holdTimeSeconds = holdTimeSeconds;
    }

    public String getName() { return name; }
    public BigDecimal getTargetTemperatureCelsius() { return targetTemperatureCelsius; }
    public Integer getHoldTimeSeconds() { return holdTimeSeconds; }
}
