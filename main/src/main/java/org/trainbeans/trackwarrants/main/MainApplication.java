package org.trainbeans.trackwarrants.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.trainbeans.trackwarrants.main")
@EnableJpaRepositories(basePackages = "org.trainbeans.trackwarrants.main.repository")
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
