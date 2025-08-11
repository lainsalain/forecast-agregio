package com.agregio.forecast.controllers;

import com.agregio.forecast.dto.ForecastPage;
import com.agregio.forecast.entities.Forecast;
import com.agregio.forecast.services.ForecastService;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("forecasts")
public class ForecastController {
    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping
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

    @GetMapping("/average")
    public ResponseEntity<?> getAverageAtSpecificTime(
            @RequestParam("perimeter") String perimeter,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime time
    ) {
        Optional<Double> average = forecastService.getAverageAtSpecificTime(perimeter, time);
        if(average.isEmpty()) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "error", "not_found",
                            "message", "No data has been found.",
                            "perimeter", perimeter,
                            "time", time.toString()
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "perimeter", perimeter,
                        "time", time.toString(),
                        "average", average
                )
        );
    }
}
