package com.agregio.forecast.services;

import com.agregio.forecast.dto.ForecastPage;
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
import java.util.List;
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
    void getForecastPage_returnsFirstPage_withMoreData() throws IllegalAccessException {
        OffsetDateTime start = OffsetDateTime.of(2025,7,1,0,0,0,0, ZoneOffset.UTC);
        OffsetDateTime end = start.plusDays(7);

        Forecast forecast1 = new Forecast();
        forecast1.setTime(start.plusHours(1));
        Forecast forecast2 = new Forecast();
        forecast2.setTime(start.plusHours(2));
        given(forecastRepository.findFirstPage(any(OffsetDateTime.class), any(OffsetDateTime.class),  any(PageRequest.class))).willReturn(List.of(forecast1, forecast2));
        given(forecastRepository.existsByTimeGreaterThanAndTimeLessThan(any(OffsetDateTime.class), any(OffsetDateTime.class))).willReturn(true);

        ForecastPage result = forecastService.getForecastPage(start, end, 1, null);
        assertTrue(result.forecasts().contains(forecast1));
        assertTrue(result.forecasts().contains(forecast2));
        assertTrue(result.hasMoreData());
        assertEquals(forecast2.getTime(), result.nextStartTime());
    }

    @Test
    void getForecasts_throwsIllegalArgumentException() {
        OffsetDateTime start = OffsetDateTime.of(2025,7,1,0,0,0,0, ZoneOffset.UTC);
        assertThrows(IllegalArgumentException.class,
                () -> forecastService.getForecastPage(start, start.minusDays(1), 10, null));
        assertThrows(IllegalArgumentException.class,
                () -> forecastService.getForecastPage(null, start.plusDays(1), 10, null));
        assertThrows(IllegalArgumentException.class,
                () -> forecastService.getForecastPage(start, start.plusDays(1), 0, null));
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
