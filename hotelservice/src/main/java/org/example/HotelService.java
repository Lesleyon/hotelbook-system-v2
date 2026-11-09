package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(scanBasePackages = "org.example") // Сканируем всё
@EnableDiscoveryClient
public class HotelService {
    public static void main(String[] args) {
        SpringApplication hotel_app = new SpringApplication(HotelService.class);
        Map<String, Object> hotel_properties = new HashMap<>();
        hotel_properties.put("server.port", "8082");
        hotel_properties.put("spring.application.name", "hotel-service");
        hotel_properties.put("eureka.client.service-url.defaultZone", "http://localhost:8761/eureka/");
        hotel_properties.put("eureka.instance.hostname", "localhost");
        hotel_properties.put("spring.datasource.url", "jdbc:h2:mem:hoteldb");
        hotel_properties.put("spring.datasource.driverClassName", "org.h2.Driver");
        hotel_properties.put("spring.datasource.username", "sa");
        hotel_properties.put("spring.datasource.password", "");
        hotel_properties.put("spring.jpa.hibernate.ddl-auto", "create-drop");
        hotel_properties.put("spring.jpa.show-sql", "true");
        hotel_properties.put("spring.autoconfigure.exclude", "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration");
        hotel_app.setDefaultProperties(hotel_properties);
        hotel_app.run(args);
    }
}