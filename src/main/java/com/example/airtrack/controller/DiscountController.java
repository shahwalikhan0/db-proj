package com.example.airtrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DiscountController {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @PostMapping("/admin/discount/remove")
    public String removeDiscount(@RequestParam("discountCode") String code, Model model) {
        String sql = "DELETE FROM discount WHERE code = ? AND passengerId IS NULL";
        boolean isDeleted = jdbcTemplate.update(sql, code) > 0;
        if (isDeleted)
            model.addAttribute("message", "Discount removed successfully");
        else
            model.addAttribute("message", "Discount not found or already used");
        return "/admin/management";
    }
    @PostMapping("/admin/discount/update")
    public String updateDiscount(@RequestParam("discountCode") String code,
                                 @RequestParam("discountPercent") int percent,
                                 Model model) {
        if(percent < 0 || percent > 100) {
            model.addAttribute("message", "Discount percent must be between 0 and 100");
            return "/admin/management";
        }
        String sql = "UPDATE discount SET discountPercent = ? WHERE code = ? AND passengerId IS NULL";
        boolean isUpdated = jdbcTemplate.update(sql, percent, code) > 0;
        if (isUpdated)
            model.addAttribute("message", "Discount updated successfully");
        else
            model.addAttribute("message", "Discount not found or already used");
        return "/admin/management";
    }
    @PostMapping("/admin/discount/add")
    public String addDiscount(@RequestParam("discountCode") String code,
                              @RequestParam("discountPercent") int percent,
                              Model model) {
        if(percent < 0 || percent > 100) {
            model.addAttribute("message", "Discount percent must be between 0 and 100");
            return "/admin/management";
        }
        String sql = "INSERT INTO discount (code, discountPercent) VALUES (?, ?)";
        boolean isAdded = jdbcTemplate.update(sql, code, percent) > 0;
        if (isAdded)
            model.addAttribute("message", "Discount added successfully");
        else
            model.addAttribute("message", "Discount already exists");
        return "/admin/management";
    }
}
