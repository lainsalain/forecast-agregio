package com.agregio.forecast.controllers;

import com.agregio.forecast.entities.Forecast;
import com.agregio.forecast.exceptions.ExceptionsHandler;
import com.agregio.forecast.services.ForecastService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(ExceptionsHandler.class)
public class ForecastControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ForecastService forecastService;

    @Test
    void getForecasts_ok_returnsForecasts() throws Exception {
        Forecast forecast = new Forecast();
        forecast.setTime(OffsetDateTime.parse("2025-08-10T00:00:00Z"));
        forecast.setPerimeter("Perimeter");
        forecast.setForecaster("Forecaster");
        forecast.setSubFeature("SubFeature");
        forecast.setUnit("Unit");
        forecast.setValue(1.00);

        Slice<Forecast> forecastSlice = new SliceImpl<>(Collections.singletonList(forecast), PageRequest.of(0, 1), false);
        given(forecastService.getForecasts(any(OffsetDateTime.class), any(OffsetDateTime.class), anyInt(), anyInt())).willReturn(forecastSlice);

        mockMvc.perform(get("/forecasts")
            .param("start_date_time", "2025-08-09T00:00:00Z")
            .param("end_date_time", "2025-08-11T00:00:00Z")
            .param("limit", "1")
            .param("offset", "0")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.forecasts[0].perimeter").value("Perimeter"))
                .andExpect(jsonPath("$.limit").value("1"))
                .andExpect(jsonPath("$.offset").value("0"))
                .andExpect(jsonPath("$.hasMoreData").value(false));

    }

    @Test
    void getForecasts_serviceThrowsException_returns400() throws Exception {
        given(forecastService.getForecasts(any(OffsetDateTime.class), any(OffsetDateTime.class), anyInt(), anyInt()))
                .willThrow(new IllegalArgumentException("Limit must be between 1 and 200."));
        mockMvc.perform(get("/forecasts")
                        .param("start_date_time", "2025-08-09T00:00:00Z")
                        .param("end_date_time", "2025-08-11T00:00:00Z")
                        .param("limit", "201")
                        .param("offset", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("bad_request"))
                .andExpect(jsonPath("$.message").value("Limit must be between 1 and 200."));
    }

    @Test
    void getAverageAtSpecificTime_ok_returnsAverage() throws Exception {
        given(forecastService.getAverageAtSpecificTime(anyString(), any(OffsetDateTime.class))).willReturn(Optional.of(50.00));
        mockMvc.perform(get("/forecasts/average")
                        .param("perimeter", "Perimeter")
                        .param("time", "2025-08-09T00:00:00Z")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perimeter").value("Perimeter"))
                .andExpect(jsonPath("$.average").value(50.00));
    }

    @Test
    void getAverageAtSpecificTime_notFound() throws Exception {
        given(forecastService.getAverageAtSpecificTime(anyString(), any(OffsetDateTime.class))).willReturn(Optional.empty());
        mockMvc.perform(get("/forecasts/average")
                        .param("perimeter", "Perimeter")
                        .param("time", "2025-08-09T00:00:00Z")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }
}
