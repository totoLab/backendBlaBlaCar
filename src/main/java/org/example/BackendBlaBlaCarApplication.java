package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.example.controllers"})
public class BackendBlaBlaCarApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendBlaBlaCarApplication.class, args);
    }

}
