package com.sparta.mulmul;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class MulmulApplication {

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.properties,"
            + "classpath:application.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(MulmulApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }

    @PostConstruct
    public void started(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    static { System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true"); }

}
