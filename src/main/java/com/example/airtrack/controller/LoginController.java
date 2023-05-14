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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;

@Controller
public class LoginController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("/")
    public String navigateHomePage(HttpSession session) {
        session.removeAttribute("user");
        return "/general/login";
    }

    @PostMapping("/")
    public String login(@RequestParam("userType") String userType,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model,
                        HttpSession session) throws SQLException {
        switch (userType) {
            case "ADMIN" -> {
                Admin user = new Admin(jdbcTemplate);
                if (user.login(username, password, 1)) {
                    user.setAdmin(true);
                    user.setId(user.getUserId(username));
                    user.setUsername(username);
                    session.setAttribute("user", user);
                    return "redirect:/admin/dashboard";
                } else {
                    model.addAttribute("errorMessage", "Invalid Admin credentials");
                }
            }
            case "PASSENGER" -> {
                Passenger user = new Passenger(jdbcTemplate);
                if (user.login(username, password, 0)) {
                    int id = user.getUserId(username);
                    user.setAdmin(false);
                    user.setPrimeMember(id);
                    user.setId(user.getUserId(username));
                    user.setUsername(username);
                    user.setPassword(password);
                    session.setAttribute("user", user);
                    return "redirect:/passenger/dashboard";
                } else {
                    model.addAttribute("errorMessage", "Invalid Passenger credentials");
                }
            }
        }
        return "/general/login";
    }
}
