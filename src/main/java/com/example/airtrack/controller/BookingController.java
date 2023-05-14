package com.example.airtrack.controller;

import com.example.airtrack.Model.Passenger;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class BookingController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @GetMapping("passenger/booking")
    public String navigatePassengerContactPage(HttpSession session, Model model) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.isAdmin())
            return "redirect:/";
        String sql = "SELECT * FROM TICKET INNER JOIN flight ON flight.id = ticket.flightId where passengerId = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, user.getId());
        model.addAttribute("bookings", rows);
        return "passenger/booking";
    }
}
