package com.example.airtrack.controller;

import com.example.airtrack.Model.Admin;
import com.example.airtrack.Model.Passenger;
import com.example.airtrack.Model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("admin/dashboard")
    public String navigateAdminDashboardPage(HttpSession session, Model model) {
        Admin user = (Admin) session.getAttribute("user");
        if (user == null || !user.isAdmin())
            return "redirect:/";

        String sql = "SELECT * FROM passenger";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        model.addAttribute("passengers", rows);
        sql = "SELECT * FROM flight";
        rows = jdbcTemplate.queryForList(sql);
        model.addAttribute("flights", rows);
        sql = "SELECT * FROM baggage";
        rows = jdbcTemplate.queryForList(sql);
        model.addAttribute("baggage", rows);
        sql = "SELECT * FROM inventory";
        rows = jdbcTemplate.queryForList(sql);
        model.addAttribute("inventory", rows);
        sql = "SELECT * FROM discount";
        rows = jdbcTemplate.queryForList(sql);
        model.addAttribute("discounts", rows);
        sql = "SELECT * FROM revenue";
        rows = jdbcTemplate.queryForList(sql);
        model.addAttribute("revenue", rows);
        model.addAttribute("name", user.getUsername());
        return "admin/dashboard";
    }

    @GetMapping("passenger/dashboard")
    public String navigatePassengerDashboardPage(HttpSession session, Model model) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.isAdmin())
            return "redirect:/";
        model.addAttribute("isPrimeMember", user.isPrimeMember());
        return "passenger/dashboard";
    }
}
