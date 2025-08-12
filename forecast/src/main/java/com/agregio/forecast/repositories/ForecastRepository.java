package com.agregio.forecast.repositories;

import com.agregio.forecast.entities.Forecast;
import com.agregio.forecast.entities.ForecastId;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ForecastRepository extends JpaRepository<Forecast, ForecastId> {

    @Query("""
            SELECT f
            FROM Forecast f
            WHERE f.time >= :start
            AND f.time < :end
            ORDER BY f.time ASC""")
    List<Forecast> findFirstPage(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, Pageable pageable);

    @Query("""
            SELECT f
            FROM Forecast f
            WHERE f.time > :start
            AND f.time < :end
            ORDER BY f.time ASC""")
    List<Forecast> findPageAfterSpecificTime(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, Pageable pageable);

    boolean existsByTimeGreaterThanAndTimeLessThan(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query("""
            SELECT AVG(f.value)
            FROM Forecast f
            WHERE f.perimeter = :perimeter
            AND f.time = :time""")
    Optional<Double> averageValueAtSpecificTime(@Param("perimeter") String perimeter, @Param("time") OffsetDateTime time);
}
