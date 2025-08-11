package com.agregio.forecast.entities;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

public class ForecastIdTest {
    @Test
    public void equalsAndHashCode_onSameValues_areEqual() {
        OffsetDateTime time = OffsetDateTime.of(2025, 8, 10, 0, 0, 0, 0, ZoneOffset.UTC);
        ForecastId forecastId1 = new ForecastId(time, "Perimeter 1", "Forecaster 1", "Sub feature 1");
        ForecastId forecastId2 = new ForecastId(time, "Perimeter 1", "Forecaster 1", "Sub feature 1");
        assertEquals(forecastId1, forecastId2);
        assertEquals(forecastId1.hashCode(), forecastId2.hashCode());
    }

    @Test
    public void equals_OnDifferentValues_isNotEqual() {
        OffsetDateTime time = OffsetDateTime.of(2025, 8, 10, 0, 0, 0, 0, ZoneOffset.UTC);
        String perimeter =  "Perimeter 1";
        String forecaster =  "Forecaster 1";
        String subFeature =  "Sub feature 1";
        ForecastId forecastId = new ForecastId(time, perimeter, forecaster, subFeature);
        assertNotEquals(forecastId, new ForecastId(time.minusHours(1), perimeter, forecaster, subFeature));
        assertNotEquals(forecastId, new ForecastId(time, "Perimeter 2", forecaster, subFeature));
        assertNotEquals(forecastId, new ForecastId(time, perimeter, "Forecaster 2", subFeature));
        assertNotEquals(forecastId, new ForecastId(time, perimeter, forecaster, "Sub feature 2"));
    }
}
