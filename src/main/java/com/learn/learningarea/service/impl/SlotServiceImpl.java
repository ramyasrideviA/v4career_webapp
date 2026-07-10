package com.learn.learningarea.service.impl;

import com.learn.learningarea.dto.TimeSlot;
import com.learn.learningarea.model.Schedule;
import com.learn.learningarea.repository.ScheduleRepository;
import com.learn.learningarea.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final ScheduleRepository scheduleRepository;

    @Override
    public List<TimeSlot> generateSlots(LocalDate date) {

        DayOfWeek day = date.getDayOfWeek();
        boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);

        List<TimeSlot> slots = new ArrayList<>();
        LocalTime start = isWeekend ? LocalTime.of(10, 0) : LocalTime.of(7, 0);
        LocalTime end = isWeekend ? LocalTime.of(13, 0) : LocalTime.of(20, 0);

        while (start.isBefore(end)) {
            slots.add(new TimeSlot(start, start.plusHours(1)));
            start = start.plusHours(1);
        }

        return slots;
    }

    @Override
    public List<Schedule> getBookingsForDate(LocalDate date) {

        List<Schedule> bookings = scheduleRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(date, date);
        
        DayOfWeek day = date.getDayOfWeek();
        boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
        
        return bookings.stream()
                .filter(s -> {
                    if ("Week Days".equals(s.getBatchType())) return !isWeekend;
                    if ("Weekend".equals(s.getBatchType())) return isWeekend;
                    return true;
                })
                .toList();
    }

    @Override
    public List<TimeSlot> getAvailableSlots(LocalDate date) {

        List<TimeSlot> allSlots = generateSlots(date);

        List<Schedule> bookedSchedules = getBookingsForDate(date);

        return allSlots.stream()
                .filter(slot -> bookedSchedules.stream()
                        .noneMatch(schedule -> overlaps(slot, schedule)))
                .toList();
    }

    private boolean overlaps(TimeSlot slot, Schedule schedule) {

        return slot.getStartTime().isBefore(schedule.getEndTime()) && 
               slot.getEndTime().isAfter(schedule.getStartTime());
    }
}
