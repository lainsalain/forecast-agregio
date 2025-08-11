package com.agregio.forecast.controllers;

import com.agregio.forecast.dto.ForecastPage;
import com.agregio.forecast.entities.Forecast;
import com.agregio.forecast.services.ForecastService;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.OffsetDateTime;

@RestController
public class ForecastController {
    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping("/forecasts")
    public ResponseEntity<ForecastPage> getForecasts(
            @RequestParam("start_date_time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam("end_date_time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
            @RequestParam(value = "limit", defaultValue = "200") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset
    ) {
        Slice<Forecast> forecastSlice = forecastService.getForecasts(start, end, limit, offset);
        ForecastPage forecastPage = new ForecastPage(forecastSlice.getContent(), limit, offset, forecastSlice.hasNext());
        return ResponseEntity.ok(forecastPage);
    }
}
