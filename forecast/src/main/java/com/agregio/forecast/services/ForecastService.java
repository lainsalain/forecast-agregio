package com.agregio.forecast.services;

import com.agregio.forecast.dto.ForecastPage;
import com.agregio.forecast.entities.Forecast;
import com.agregio.forecast.repositories.ForecastRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ForecastService {
    private final ForecastRepository forecastRepository;
    private static final int MAX_LIMIT = 200;

    public ForecastService(ForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }

    /**
     * Get the forecasts between start & end timestamp
     * @param start
     * @param end
     * @param limit
     * @param nextStartTime
     * @return
     */
    @Transactional(readOnly = true)
    public ForecastPage getForecastPage(OffsetDateTime start, OffsetDateTime end, int limit, OffsetDateTime nextStartTime) {
        if (limit <= 0)
            throw new IllegalArgumentException("Limit must be > 0.");

        if (limit > MAX_LIMIT)
            throw new IllegalArgumentException("Limit can not exceed " + MAX_LIMIT + ".");

        if (start == null || end == null)
            throw new IllegalArgumentException("Start and end time must not be null.");

        if (end.isBefore(start))
            throw new IllegalArgumentException("Start date must be after end date.");

        Pageable pageable = PageRequest.of(0, limit);
        List<Forecast> forecasts = nextStartTime == null
                ? forecastRepository.findFirstPage(start, end, pageable)
                : forecastRepository.findPageAfterSpecificTime(nextStartTime, end, pageable);

        OffsetDateTime newStartTime = forecasts.isEmpty() ? null : forecasts.get(forecasts.size() - 1).getTime();
        boolean hasMoreData = newStartTime != null && forecastRepository.existsByTimeGreaterThanAndTimeLessThan(newStartTime, end);
        return new ForecastPage(forecasts, limit, hasMoreData, newStartTime);
    }

    /**
     * Get the forecasts average value at a specific timestamp
     * @param perimeter
     * @param time
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<Double> getAverageAtSpecificTime(String perimeter, OffsetDateTime time) {
        if (perimeter == null) {
            throw new IllegalArgumentException("Perimeter must not be null.");
        }
        if (time == null) {
            throw new IllegalArgumentException("Time must not be null.");
        }

        return forecastRepository.averageValueAtSpecificTime(perimeter, time);
    }
}
