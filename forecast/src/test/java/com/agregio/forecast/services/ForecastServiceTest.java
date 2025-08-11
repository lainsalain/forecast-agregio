package com.agregio.forecast.services;

import com.agregio.forecast.entities.Forecast;
import com.agregio.forecast.repositories.ForecastRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ForecastServiceTest {
    ForecastService forecastService;
    ForecastRepository forecastRepository;

    @BeforeEach
    void setUp() {
        forecastRepository = mock(ForecastRepository.class);
        forecastService = new ForecastService(forecastRepository);
    }

    @Test
    void getForecasts_returnsSlicedForecasts() throws IllegalAccessException {
        OffsetDateTime start = OffsetDateTime.of(2025,7,1,0,0,0,0, ZoneOffset.UTC);
        OffsetDateTime end = start.plusDays(7);

        Forecast forecast = new Forecast();
        forecast.setTime(start.plusHours(1));
        Slice<Forecast> forecastSlice = new SliceImpl<>(Collections.singletonList(forecast), PageRequest.of(0, 1), false);
        given(forecastRepository.findByTimeRange(any(OffsetDateTime.class), any(OffsetDateTime.class),  any(PageRequest.class))).willReturn(forecastSlice);

        Slice<Forecast> result = forecastService.getForecasts(start, end, 1, 0);
        assertEquals(1, result.getContent().size());
        assertFalse(result.hasNext());
    }

    @Test
    void getForecasts_throwsIllegalArgumentException() {
        OffsetDateTime start = OffsetDateTime.of(2025,7,1,0,0,0,0, ZoneOffset.UTC);
        assertThrows(IllegalArgumentException.class,
                () -> forecastService.getForecasts(start, start.minusDays(1), 10, 0));
        assertThrows(IllegalArgumentException.class,
                () -> forecastService.getForecasts(start, start.plusDays(1), 10, -10));
        assertThrows(IllegalArgumentException.class,
                () -> forecastService.getForecasts(start, start.plusDays(1), 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> forecastService.getForecasts(start, start.plusDays(1), 201, 0));
        verifyNoInteractions(forecastRepository);
    }

    @Test
    void getAverageAtSpecificTime_returnsAverage() {
        OffsetDateTime time = OffsetDateTime.of(2025,8,10,0,0,0,0, ZoneOffset.UTC);
        given(forecastRepository.averageValueAtSpecificTime(anyString(), any(OffsetDateTime.class))).willReturn(Optional.of(50.00));

        Optional<Double> result = forecastService.getAverageAtSpecificTime("Perimeter", time);
        assertEquals(Optional.of(50.00), result);
    }

    @Test
    void getAverageAtSpecificTime_throwsIllegalArgumentException() {
        OffsetDateTime time = OffsetDateTime.of(2025,8,10,0,0,0,0, ZoneOffset.UTC);
        assertThrows(IllegalArgumentException.class,
                () -> forecastService.getAverageAtSpecificTime(null, time));
        assertThrows(IllegalArgumentException.class,
                () -> forecastService.getAverageAtSpecificTime("Perimeter", null));
    }
}
