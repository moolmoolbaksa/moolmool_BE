package com.sparta.mulmul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MulmulApplication {

    public static void main(String[] args) {
        SpringApplication.run(MulmulApplication.class, args);
    }

}
