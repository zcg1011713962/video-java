package org.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
