package com.example.airtrack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AirtrackApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AirtrackApplication.class, args);
    }
    public void run(String... args) throws Exception {
        System.out.println("Staring...!");
    }

}
