package com.example.airtrack.controller;

import com.example.airtrack.Model.Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ManagementController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("admin/management")
    public String navigateAdminManagementPage(HttpSession session, Model model) {
        Admin user = (Admin) session.getAttribute("user");
        if (user == null || !user.isAdmin())
            return "redirect:/";
        return "admin/management";
    }
}
