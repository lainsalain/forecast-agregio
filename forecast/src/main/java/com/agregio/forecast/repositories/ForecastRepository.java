package com.agregio.forecast.repositories;

import com.agregio.forecast.entities.Forecast;
import com.agregio.forecast.entities.ForecastId;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface ForecastRepository extends JpaRepository<Forecast, ForecastId> {
    @Query("""
            SELECT f FROM Forecast f
            WHERE f.time >= :start
            AND f.time < :end
            ORDER BY f.time ASC""")
    Slice<Forecast> findByTimeRange(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, Pageable pageable);
}
