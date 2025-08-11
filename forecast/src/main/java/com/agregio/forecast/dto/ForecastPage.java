package com.agregio.forecast.dto;

import com.agregio.forecast.entities.Forecast;

import java.util.List;

public record ForecastPage(List<Forecast> forecasts, int limit, int offset, boolean hasMoreData) {
}
