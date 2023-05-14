package com.example.airtrack.Model;
import com.example.airtrack.Dao.UserDao;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

public abstract class User {

    private int userID;
    private String username;
    private String password;
    protected boolean isAdmin;
    protected JdbcTemplate jdbcTemplate;


    public User(int userID, String username, String password, JdbcTemplate jdbcTemplate) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.jdbcTemplate = jdbcTemplate;
    }
    public User(String username, String password, JdbcTemplate jdbcTemplate) {
        this.username = username;
        this.password = password;
        this.jdbcTemplate = jdbcTemplate;
    }
    public User() {

    }
    //    public boolean signUp(int phone, String userType) throws SQLException {
//        return true;
//    }
    public boolean logout() throws SQLException {
        return false;
    }
    public int getId() {
        return userID;
    }
    //getters
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
    //setters
    public void setPassword(String pass){
        this.password = pass;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void setId(int userId) {
        this.userID = userId;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean login(String username, String password, int type) {
        UserDao userDao = new UserDao(jdbcTemplate);
        return userDao.login(username, password, type);
    }

    public int getUserId(String username) {
        UserDao userDao = new UserDao(jdbcTemplate);
        return userDao.getUserId(username);
    }
}
