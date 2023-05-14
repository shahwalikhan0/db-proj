package com.example.airtrack.controller;

import java.sql.SQLException;

import com.example.airtrack.Dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignupController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/general/signup")
    public String navigateSignupPage() {
        return "general/signup";
    }
    @PostMapping("/general/signup")
    public String signup(@RequestParam("username") String username,
                         @RequestParam("password") String password,
                         @RequestParam("confirmPassword") String confirmPassword,
                         @RequestParam("passportNumber") String passportNumber,
                         @RequestParam("fullName") String fullName,
                         Model model) throws SQLException {
        if (!password.equals(confirmPassword) || password.length() < 6) {
            model.addAttribute("errorMessage", "Passwords do not match or are less than 6 characters");
            return "/general/signup";
        }

        if(username.contains(" ") || username.length() < 6) {
            model.addAttribute("errorMessage", "Username cannot contain spaces and must be at least 6 characters long");
            return "/general/signup";
        }
        UserDao userDao = new UserDao(jdbcTemplate);

        if (userDao.isUser(username)) {
            model.addAttribute("errorMessage", "Username already exists");
            return "/general/signup";
        }
        userDao.addUser(username, password, passportNumber, fullName);
        model.addAttribute("successMessage", "Account created successfully");
        return "/general/signup";
    }
}
