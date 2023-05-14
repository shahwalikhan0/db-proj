package com.example.airtrack.controller;

import com.example.airtrack.Model.Admin;
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
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class FlightController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("passenger/flight")
    public String navigatePassengerFlightPage(HttpSession session, Model model) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null || user.isAdmin())
            return "redirect:/";

        String sql = "SELECT * FROM userAccount WHERE id = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, user.getId());
        BigDecimal balance = (BigDecimal) rows.get(0).get("balance");
        model.addAttribute("balance", balance);
        model.addAttribute("message", session.getAttribute("message"));
        session.removeAttribute("message");

        if(session.getAttribute("flights") != null) {
            model.addAttribute("flights", session.getAttribute("flights"));
            session.removeAttribute("flights");
            return "passenger/flight";
        }
        sql = "SELECT * FROM flight where departureTime >= ?";
        rows = jdbcTemplate.queryForList(sql, LocalDate.now());
        model.addAttribute("flights", rows);
        return "passenger/flight";
    }
    @GetMapping("/admin/flight/add")
    public String navAdminAddFlight(Model model){
        LocalDate currentDate = LocalDate.now().plusDays(1);
        model.addAttribute("currentDate", currentDate);
        return "admin/management";
    }

    @PostMapping("/passenger/flight/book")
    public  String bookFlight(@RequestParam("id") int id,
                              @RequestParam("seats") int seats,
                              HttpSession session, Model model) {
        Passenger user = (Passenger) session.getAttribute("user");
        String sql = "SELECT balance FROM userAccount WHERE id = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, user.getId());
        BigDecimal balance = (BigDecimal) rows.get(0).get("balance");
        sql = "SELECT * FROM flight WHERE id = ?";
        rows = jdbcTemplate.queryForList(sql, id);
        int availableSeats = (int) rows.get(0).get("numOfSeats");
        Integer price = (Integer) rows.get(0).get("price");
        if (seats > availableSeats) {
            session.setAttribute("message", "Not enough seats available");
            return "redirect:/passenger/flight";
        }
        BigDecimal totalCost = BigDecimal.valueOf(price * seats);
        if (balance.compareTo(totalCost) < 0) {
            session.setAttribute("message", "Not enough balance");
            return "redirect:/passenger/flight";
        }
        sql = "UPDATE userAccount SET balance = ? WHERE id = ?";
        boolean isUpdated = jdbcTemplate.update(sql, balance.subtract(totalCost), user.getId()) > 0;
        if (!isUpdated) {
            model.addAttribute("message", "Something went wrong");
            return "/passenger/flight";
        }
        sql = "UPDATE flight SET numOfSeats = ? WHERE id = ?";
        isUpdated = jdbcTemplate.update(sql, availableSeats - seats, id) > 0;
        if (!isUpdated) {
            model.addAttribute("message", "Something went wrong");
            return "/passenger/flight";
        }
        sql = "INSERT INTO ticket (passengerId, flightId, seatsBooked) VALUES (?, ?, ?)";
        isUpdated = jdbcTemplate.update(sql, user.getId(), id, seats) > 0;
        if (!isUpdated) {
            model.addAttribute("message", "Something went wrong");
            return "/passenger/flight";
        }
        session.setAttribute("message", "Flight booked successfully");
        return "redirect:/passenger/flight";
    }
    @PostMapping("/admin/flight/remove")
    public String removeFlight(@RequestParam("id") int id, Model model) {
        String sql = "SELECT * FROM flight WHERE id = ?";
        List<Map<String, Object>> flights = jdbcTemplate.queryForList(sql, id);
        if (flights.size() == 0) {
            model.addAttribute("message", "Flight not found");
            return "/admin/management";
        }
        int totalSeats = (int) flights.get(0).get("totalSeats");
        int availableSeats = (int) flights.get(0).get("numOfSeats");
        if (totalSeats != availableSeats) {
            model.addAttribute("message", "Flight cannot be removed. Some seats are already booked");
            return "/admin/management";
        }
        sql = "DELETE FROM flight WHERE id = ?";
        boolean isDeleted = jdbcTemplate.update(sql, id) > 0;
        if (isDeleted)
            model.addAttribute("message", "Flight removed successfully");
        else
            model.addAttribute("message", "Flight not found");
        return "/admin/management";
    }
    @PostMapping("/admin/flight/update")
    public String updateFlight(@RequestParam("id") int id,
                               @RequestParam("departureTime") Date departureTime,
                               @RequestParam("arrivalTime") Date arrivalTime,
                                Model model) {

        String sql = "SELECT * FROM flight WHERE id = ?";
        List<Map<String, Object>> flights = jdbcTemplate.queryForList(sql, id);
        if (flights.size() == 0) {
            model.addAttribute("message", "Flight not found");
            return "/admin/management";
        }
        if (verifyDate(departureTime, arrivalTime, model))
            return "/admin/management";
        sql = "UPDATE flight SET departureTime = ?, arrivalTime = ? WHERE id = ?";
        boolean isUpdated = jdbcTemplate.update(sql, departureTime, arrivalTime, id) > 0;
        if (isUpdated)
            model.addAttribute("message", "Flight updated successfully");
        else
            model.addAttribute("message", "Flight not found");
        return "/admin/management";
    }

    private boolean verifyDate(@RequestParam("departureTime") Date departureTime, @RequestParam("arrivalTime") Date arrivalTime, Model model) {
        LocalDate currentDate = LocalDate.now().plusDays(1);
        if (departureTime.toLocalDate().isBefore(currentDate) || arrivalTime.toLocalDate().isBefore(currentDate)) {
            model.addAttribute("message", "Date must be greater than current date by at least 1 day");
            return true;
        }
        if (departureTime.toLocalDate().isAfter(arrivalTime.toLocalDate())) {
            model.addAttribute("message", "Departure date must be before arrival date");
            return true;
        }
        return false;
    }

    @PostMapping("/admin/flight/add")
    public String addFlight(@RequestParam("departureTime") Date departureTime,
                            @RequestParam("arrivalTime") Date arrivalTime,
                            @RequestParam("departureAirport") String departureAirport,
                            @RequestParam("destinationAirport") String arrivalAirport,
                            @RequestParam("totalSeats") int totalSeats,
                            @RequestParam("price") int price,
                            Model model) {
        if (verifyDate(departureTime, arrivalTime, model)) return "/admin/management";
        if (totalSeats < 0) {
            model.addAttribute("message", "Total seats must be greater than 0");
            return "/admin/management";
        }
        String sql = "INSERT INTO flight (departureTime, arrivalTime, departureAirport, destinationAirport, totalSeats, numOfSeats, price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        boolean isAdded = jdbcTemplate.update(sql, departureTime, arrivalTime, departureAirport, arrivalAirport, totalSeats, totalSeats, price) > 0;
        if (isAdded)
            model.addAttribute("message", "Flight added successfully");
        else
            model.addAttribute("message", "Failed to add flight");
        return "/admin/management";
    }
    @PostMapping("/passenger/flight/search")
    public String searchItem(HttpSession session, @RequestParam("search") String search){
        String sql = "SELECT * FROM flight WHERE departureAirport LIKE ? OR destinationAirport LIKE ? OR departureTime LIKE ? OR arrivalTime LIKE ? OR price LIKE ?";
        List<Map<String, Object>> flights = jdbcTemplate.queryForList(sql, "%" + search + "%", "%" + search + "%", "%" + search + "%", "%" + search + "%", "%" + search + "%");
        session.setAttribute("flights", flights);
        return "redirect:/passenger/flight";
    }
    @PostMapping("/passenger/flight/filter")
    public String filterItem(HttpSession session, @RequestParam("departureAirport") String departureAirport,
                             @RequestParam("destinationAirport") String destinationAirport,
                             @RequestParam(value = "price", required = false) Integer price){
        String sql;
        if(departureAirport.equals("")) {
            sql = "SELECT * FROM flight WHERE destinationAirport LIKE ?";
            List<Map<String, Object>> flights = jdbcTemplate.queryForList(sql, "%" + destinationAirport + "%");
            session.setAttribute("flights", flights);
            return "redirect:/passenger/flight";
        }
        if(destinationAirport.equals("")) {
            sql = "SELECT * FROM flight WHERE departureAirport LIKE ?";
            List<Map<String, Object>> flights = jdbcTemplate.queryForList(sql, "%" + departureAirport + "%");
            session.setAttribute("flights", flights);
            return "redirect:/passenger/flight";

        }
        sql = "SELECT * FROM flight WHERE departureAirport LIKE ? AND destinationAirport LIKE ? AND price = ?";
        List<Map<String, Object>> flights = jdbcTemplate.queryForList(sql, "%" + departureAirport + "%", "%" + destinationAirport + "%", price);
        session.setAttribute("flights", flights);
        return "redirect:/passenger/flight";
    }
}
