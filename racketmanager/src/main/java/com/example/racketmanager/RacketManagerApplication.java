package com.example.racketmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.racketmanager")
public class RacketManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RacketManagerApplication.class, args);
    }
}
