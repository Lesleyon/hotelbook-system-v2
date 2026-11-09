package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient
public class BoockingMain {
    public static void main(String[] args) {
        SpringApplication boocking_app = new SpringApplication(BoockingMain.class);
        Map<String, Object> boocking_properties = new HashMap<>();
        boocking_properties.put("server.port", "8083");
        boocking_properties.put("eureka.client.service-url.defaultZone", "http://localhost:8761/eureka/");
        boocking_properties.put("eureka.instance.hostname", "localhost");
        boocking_properties.put("spring.application.name", "booking-service");
        boocking_properties.put("spring.datasource.url", "jdbc:h2:mem:bookingdb");
        boocking_properties.put("spring.datasource.driverClassName", "org.h2.Driver");
        boocking_properties.put("spring.datasource.username", "sa");
        boocking_properties.put("spring.datasource.password", "");
        boocking_properties.put("spring.jpa.hibernate.ddl-auto", "create-drop");
        boocking_properties.put("spring.jpa.show-sql", "true");
        boocking_properties.put("spring.autoconfigure.exclude",
                "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration");
        boocking_app.setDefaultProperties(boocking_properties);
        boocking_app.run(args);
    }
}