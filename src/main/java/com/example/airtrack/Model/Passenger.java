package com.example.airtrack.Model;

import org.springframework.jdbc.core.JdbcTemplate;

public class Passenger extends User{
    private String fullName;
    private String passportNumber;
    private boolean isPrimeMember;

    public Passenger(String fullName, String passportNumber, boolean isPrimeMember, JdbcTemplate jdbcTemplate) {
        this.fullName = fullName;
        this.passportNumber = passportNumber;
        this.isPrimeMember = isPrimeMember;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Passenger(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }
    public  boolean isPrimeMember() {
        return isPrimeMember;
    }

    public void setPrimeMember(int id) {
        String sql = "SELECT isPrimeMember FROM [PASSENGER] WHERE id = ?";
        this.isPrimeMember = jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    public String getPassportNumber() {
        String sql = "SELECT passportNumber FROM [PASSENGER] WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, this.getId());
    }
}
