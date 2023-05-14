package com.example.airtrack.controller;

import com.example.airtrack.Dao.UserDao;
import com.example.airtrack.Model.Passenger;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class ProfileController {
    @Autowired
    JdbcTemplate  jdbcTemplate;
    @GetMapping("passenger/profile")
    public String navigatePassengerContactPage(HttpSession session, Model model) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.isAdmin())
            return "redirect:/";
        String sql = "SELECT * FROM userAccount WHERE id = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, user.getId());
        BigDecimal balance = (BigDecimal) rows.get(0).get("balance");
        model.addAttribute("balance", balance);
        model.addAttribute("passportNumber", user.getPassportNumber());
        model.addAttribute("user", user);
        model.addAttribute("message", session.getAttribute("message"));
        session.removeAttribute("message");
        return "passenger/profile";
    }
    @PostMapping("/admin/passenger/remove")
    public String removePassenger(@RequestParam("id") int id,
                                 Model model) {
        String sql = "SELECT * FROM ticket INNER JOIN flight ON ticket.flightId = flight.id WHERE ticket.passengerId = ? AND flight.departureTime > CURRENT_TIMESTAMP";
        if (jdbcTemplate.queryForList(sql, id).size() > 0) {
            model.addAttribute("message", "Passenger cannot be removed. Some tickets are already booked");
            return "/admin/management";
        }
        sql = "DELETE FROM passenger WHERE id = ?";
        boolean isRemoved = jdbcTemplate.update(sql, id) > 0;
        sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
        if (isRemoved)
            model.addAttribute("message", "Passenger removed successfully");
        else
            model.addAttribute("message", "Passenger not found");
        return "/admin/management";
    }
    @PostMapping("/admin/passenger/grant")
    public String addMembership(@RequestParam("id") int id, Model model) {
        String sql = "UPDATE passenger SET isPrimeMember = 1 WHERE id = ?";
        boolean isUpdated = jdbcTemplate.update(sql, id) > 0;
        if (isUpdated)
            model.addAttribute("message", "Membership granted successfully");
        else
            model.addAttribute("message", "Passenger not found");
        return "/admin/management";
    }

    @PostMapping("/passenger/updatePassword")
    public String updatePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmNewPassword") String confirmPassword,
                                 HttpSession session,
                                 Model model) {
        System.out.println(oldPassword + " " + newPassword + " " + confirmPassword);
        if(oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            session.setAttribute("message", "All fields are required");
            return "redirect:/passenger/profile";
        }
        Passenger user = (Passenger) session.getAttribute("user");
        if(!oldPassword.equals(user.getPassword())) {
            session.setAttribute("message", "Old password is incorrect");
            return "redirect:/passenger/profile";
        }
        if(!newPassword.equals(confirmPassword)) {
            session.setAttribute("message", "New password and confirm password must be same");
            return "redirect:/passenger/profile";
        }
        if(newPassword.length() < 8) {
            session.setAttribute("message", "Password must be at least 8 characters");
            return "redirect:/passenger/profile";
        }
        user.setPassword(newPassword);
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        jdbcTemplate.update(sql, newPassword, user.getId());
        session.setAttribute("message", "Password updated successfully");
        return "redirect:/passenger/profile";
    }
    @PostMapping("/passenger/updateProfile")
    public String updateProfile(@RequestParam("username") String username, HttpSession session){
        if(username.contains(" ") || username.length() < 6) {
            session.setAttribute("message", "Username must be at least 6 characters and no spaces");
            return "redirect:/passenger/profile";
        }
        UserDao userDao = new UserDao(jdbcTemplate);
        if (userDao.isUser(username)) {
            session.setAttribute("message", "Username already exists");
            return "redirect:/passenger/profile";
        }
        Passenger user = (Passenger) session.getAttribute("user");
        user.setUsername(username);
        String sql = "UPDATE users SET username = ? WHERE id = ?";
        jdbcTemplate.update(sql, username, user.getId());
        session.setAttribute("message", "Username updated successfully");
        return "redirect:/passenger/profile";
    }
}
