package com.agregio.forecast.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ForecastId implements Serializable {
    private OffsetDateTime time;
    private String perimeter;
    private String forecaster;
    private String subFeature;
}
