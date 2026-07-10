package com.learn.learningarea.service;

import com.learn.learningarea.dto.TimeSlot;
import com.learn.learningarea.model.Schedule;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface SlotService {

    List<TimeSlot> generateSlots(LocalDate selectedDate);

    List<Schedule> getBookingsForDate(LocalDate selectedDate);

    List<TimeSlot> getAvailableSlots(LocalDate selectedDate);
}
