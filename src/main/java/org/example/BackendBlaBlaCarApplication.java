package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class BackendBlaBlaCarApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendBlaBlaCarApplication.class, args);
    }

}
