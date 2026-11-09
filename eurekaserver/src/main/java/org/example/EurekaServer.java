package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServer {
    public static void main(String[] args) {
        SpringApplication server_app = new SpringApplication(EurekaServer.class);
        Map<String, Object> server_properties = new HashMap<>();
        server_properties.put("server.port", "8761");
        server_properties.put("eureka.client.register-with-eureka", "false");
        server_properties.put("eureka.client.fetch-registry", "false");
        server_app.setDefaultProperties(server_properties);
        server_app.run(args);
    }
}