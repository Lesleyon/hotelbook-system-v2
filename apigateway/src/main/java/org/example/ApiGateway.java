package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGateway {
    public static void main(String[] args) {
        SpringApplication api_app = new SpringApplication(ApiGateway.class);
        Map<String, Object> api_properties = new HashMap<>();
        api_properties.put("server.port", "8080");
        api_properties.put("eureka.client.service-url.defaultZone", "http://localhost:8761/eureka/");
        api_properties.put("spring.application.name", "api-gateway");
        api_app.setDefaultProperties(api_properties);
        api_app.run(args);
    }
}