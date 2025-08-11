package com.agregio.forecast.services;

import com.agregio.forecast.entities.Forecast;
import com.agregio.forecast.repositories.ForecastRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class ForecastService {
    private final ForecastRepository forecastRepository;
    private static final int MAX_LIMIT = 200;

    public ForecastService(ForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }

    @Transactional(readOnly = true)
    public Slice<Forecast> getForecasts(OffsetDateTime start, OffsetDateTime end, int limit, int offset) {
        if (limit <= 0)
            throw new IllegalArgumentException("Limit must be > 0.");

        if (limit > MAX_LIMIT)
            throw new IllegalArgumentException("Limit can not exceed " + MAX_LIMIT + ".");

        if (offset < 0)
            throw new IllegalArgumentException("Offset must be >= 0.");

        if (end.isBefore(start))
            throw new IllegalArgumentException("Start date must be after end date.");

        int page = offset / limit;
        Pageable pageable = PageRequest.of(page, limit);
        return forecastRepository.findByTimeRange(start, end, pageable);
    }


}
