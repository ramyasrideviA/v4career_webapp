package com.learn.learningarea.repository;

import com.learn.learningarea.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.startDate >= :startDate ORDER BY s.startDate DESC")
    List<Schedule> findLast32Days(@Param("startDate") java.time.LocalDate startDate);

    @Query("SELECT s FROM Schedule s WHERE " +
            "LOWER(s.batchType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.programName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(CAST(s.startTime AS string)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(CAST(s.endTime AS string)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.batchNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.studentCount) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(CAST(s.startDate AS string)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(CAST(s.endDate AS string)) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Schedule> searchSchedules(@Param("query") String query);

    List<Schedule> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    List<Schedule> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate date1, LocalDate date2);

    List<Schedule> findByBatchTypeOrderByBatchNoDesc(String batchType);
}
