// In your ShareIt.java (main application class)
package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // Enable JPA Auditing
public class ShareIt {

    public static void main(String[] args) {
        SpringApplication.run(ShareIt.class, args);
    }
}