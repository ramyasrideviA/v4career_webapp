package com.learn.learningarea.controller;

import com.learn.learningarea.dto.TimeSlot;
import com.learn.learningarea.model.Schedule;
import com.learn.learningarea.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingQuickViewController {

    private final SlotService slotService;

    @GetMapping("/quick-view")
    public String quickViewPage() {
        return "booking-quick-view";
    }

    @GetMapping("/slots")
    @ResponseBody
    public Map<String, Object> getSlots(@RequestParam String date) {

        LocalDate selectedDate = LocalDate.parse(date);

        List<Schedule> booked = slotService.getBookingsForDate(selectedDate);

        List<TimeSlot> available = slotService.getAvailableSlots(selectedDate);

        Map<String, Object> response = new HashMap<>();

        response.put("bookedSlots", booked);
        response.put("availableSlots", available);

        return response;
    }
}
