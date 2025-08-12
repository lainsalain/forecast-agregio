package com.agregio.forecast.controllers;

import com.agregio.forecast.dto.ForecastPage;
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
import java.util.List;
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
    void getForecasts_ok_returnsForecastPage() throws Exception {
        Forecast forecast1 = new Forecast();
        forecast1.setTime(OffsetDateTime.parse("2025-08-10T00:00:00Z"));
        Forecast forecast2 = new Forecast();
        forecast2.setTime(OffsetDateTime.parse("2025-08-11T00:00:00Z"));

        ForecastPage page = new ForecastPage(List.of(forecast1, forecast2), 2, true, forecast2.getTime());
        given(forecastService.getForecastPage(any(), any(), anyInt(), any())).willReturn(page);

        mockMvc.perform(get("/forecasts")
            .param("start_date_time", "2025-08-09T00:00:00Z")
            .param("end_date_time", "2025-08-12T00:00:00Z")
            .param("limit", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.forecasts[0].time").value("2025-08-10T00:00:00Z"))
                .andExpect(jsonPath("$.limit").value("2"))
                .andExpect(jsonPath("$.has_more_data").value("Yes"))
                .andExpect(jsonPath("$.next_start_time").value("2025-08-11T00:00Z"));

    }

    @Test
    void getForecasts_serviceThrowsException_returns400() throws Exception {
        given(forecastService.getForecastPage(any(), any(), anyInt(), any()))
                .willThrow(new IllegalArgumentException("Limit must be between 1 and 200."));
        mockMvc.perform(get("/forecasts")
                        .param("start_date_time", "2025-08-09T00:00:00Z")
                        .param("end_date_time", "2025-08-11T00:00:00Z")
                        .param("limit", "201")
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
