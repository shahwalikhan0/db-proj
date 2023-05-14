package com.example.airtrack.Dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public boolean login(String username, String password, int userType) {
        String sql = "SELECT * FROM [USERS] WHERE username = ? AND password = ? AND isAdmin = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, username, password, userType);
        return !rows.isEmpty();
    }

    public boolean isUser(String username) {
        String sql = "SELECT * FROM [USERS] WHERE username = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, username);
        return !rows.isEmpty();
    }

    public void addUser(String username, String password, String passportNumber, String fullName) {
        String sql = "INSERT INTO [USERS] (username, password, isAdmin) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, username, password, 0);
        sql = "SELECT id FROM [USERS] WHERE username = ?";
        int id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        sql = "INSERT INTO [PASSENGER] (id, fullName, passportNumber, isPrimeMember) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, id, fullName, passportNumber, 0);

        sql = "INSERT INTO userAccount (id, accountNumber, balance) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, id ,passportNumber,  1000);

    }
    public int getUserId(String username) {
        String sql = "SELECT id FROM [USERS] WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, username);
    }
}
