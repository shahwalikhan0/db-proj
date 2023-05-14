package com.example.airtrack.controller;

import com.example.airtrack.Model.Admin;
import com.fasterxml.jackson.databind.DatabindContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class InventoryController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping("/admin/inventory/remove")
    public String removeItem(@RequestParam("name") String name, Model model, HttpSession session) {
        Admin user = (Admin) session.getAttribute("user");
        String sql = "DELETE FROM inventory WHERE itemName = ? AND adminId = ?";
        boolean isDeleted = jdbcTemplate.update(sql, name, user.getId()) > 0;
        if (isDeleted)
            model.addAttribute("message", "Item removed successfully");
        else
            model.addAttribute("message", "Item not found for current admin");
        return "/admin/management";
    }
    @PostMapping("/admin/inventory/update")
    public String updateItem(@RequestParam("name") String name,
                                 @RequestParam("quantity") int quantity,
                                 HttpSession session,
                                 Model model) {
        if(quantity < 0) {
            model.addAttribute("message", "Quantity must be greater than 0");
            return "/admin/management";
        }
        Admin user = (Admin) session.getAttribute("user");
        String sql = "UPDATE inventory SET quantity = ? WHERE itemName = ? AND adminId = ?";
        boolean isUpdated = jdbcTemplate.update(sql, quantity, name, user.getId()) > 0;
        if (isUpdated)
            model.addAttribute("message", "Item updated successfully");
        else
            model.addAttribute("message", "Item not found for current admin");
        return "/admin/management";
    }
    @PostMapping("/admin/inventory/add")
    public String addItem(@RequestParam("name") String name,
                              @RequestParam("quantity") int quantity,
                              HttpSession session,
                              Model model) {
        if(quantity < 0) {
            model.addAttribute("message", "Quantity must be greater than 0");
            return "/admin/management";
        }

        Admin user = (Admin) session.getAttribute("user");
        String sql = "SELECT * FROM inventory WHERE itemName = ? AND adminId = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, name, user.getId());
        if(rows.size() > 0) {
            model.addAttribute("message", "Item already exists for current admin");
            return "/admin/management";
        }
        sql = "INSERT INTO inventory (itemName, quantity, adminId) VALUES (?, ?, ?)";
        boolean isAdded = jdbcTemplate.update(sql, name, quantity, user.getId()) > 0;
        if (isAdded)
            model.addAttribute("message", "Item added to inventory successfully");
        else
            model.addAttribute("message", "Failed to add item to inventory");
        return "/admin/management";
    }
    @PostMapping("/admin/revenue/add")
    public String addRevenue(@RequestParam("type") String type,
                             @RequestParam("amount") int amount,
                             HttpSession session,
                             Model model) {
        if(amount < 0) {
            model.addAttribute("message", "Amount must be greater than 0");
            return "/admin/management";
        }
        String sql = "INSERT INTO revenue (type, amount) VALUES (?, ?)";
        boolean isAdded = jdbcTemplate.update(sql, type, amount) > 0;
        if (isAdded)
            model.addAttribute("message", "Revenue added successfully");
        else
            model.addAttribute("message", "Failed to add revenue");
        return "/admin/management";
    }
    @PostMapping("/admin/revenue/update")
    public String updateRevenue(@RequestParam("id") int id,
                             @RequestParam("amount") int amount,
                             Model model) {
        if(amount < 0) {
            model.addAttribute("message", "Amount must be greater than 0");
            return "/admin/management";
        }
        String sql = "UPDATE revenue SET amount = ? WHERE revenueId = ?";
        boolean isUpdated = jdbcTemplate.update(sql, amount, id) > 0;
        if (isUpdated)
            model.addAttribute("message", "Revenue updated successfully");
        else
            model.addAttribute("message", "Failed to update revenue (not found)");
        return "/admin/management";
    }
}
