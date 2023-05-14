package com.example.airtrack.Model;

import org.springframework.jdbc.core.JdbcTemplate;

public class Admin extends User{
    private String fullName;

    public Admin(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }
}
