package edu.eci.dosw.tdd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "edu.eci.dosw.tdd.persistence.relational.repository")
@EnableMongoRepositories(basePackages = "edu.eci.dosw.tdd.persistence.nonrelational.repository")
public class DswLibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DswLibraryApplication.class, args);
    }
}
