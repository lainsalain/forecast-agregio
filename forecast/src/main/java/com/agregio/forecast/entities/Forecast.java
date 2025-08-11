package com.agregio.forecast.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "forecast")
@IdClass(ForecastId.class)
@NoArgsConstructor
@Getter
@Setter
public class Forecast implements Serializable {

    @Id
    @Column(name = "time", nullable = false)
    private OffsetDateTime time;

    @Id
    @Column(name = "perimeter", nullable = false)
    private String perimeter;

    @Id
    @Column(name = "forecaster", nullable = false)
    private String forecaster;

    @Id
    @Column(name = "sub_feature", nullable = false)
    private String subFeature;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "value", nullable = false)
    private Double value;
}