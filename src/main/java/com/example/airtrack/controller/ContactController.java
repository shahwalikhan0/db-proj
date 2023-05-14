package com.example.airtrack.controller;

import com.example.airtrack.Model.Passenger;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactController {
    @GetMapping("passenger/contact")
    public String navigatePassengerContactPage(HttpSession session, Model model) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.isAdmin())
            return "redirect:/";
        return "passenger/contact";
    }
}
