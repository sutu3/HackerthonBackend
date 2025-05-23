package com.example.hackerthon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HackerThonApplication {

    public static void main(String[] args) {
        SpringApplication.run(HackerThonApplication.class, args);
    }

}
