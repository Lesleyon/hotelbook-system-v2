package org.example;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean; // <--- Вот он
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
public class RouteConfig {
    @Bean
    public RouteLocator routes() {
        Route hotelRoute = Route.async().id("hotel-service").predicate(exchange -> {
                    String path = exchange.getRequest().getPath().value();
                    return path.equals("/api/hotels") || path.startsWith("/api/hotels/");
                }).uri("lb://HOTEL-SERVICE").build();

        Route roomsRoute = Route.async().id("hotel-rooms-public").predicate(exchange -> {
                    String path = exchange.getRequest().getPath().value();
                    return path.equals("/api/rooms") || path.equals("/api/rooms/recommend");
                }).uri("lb://HOTEL-SERVICE").build();

        Route bookingRoute = Route.async().id("booking-service").predicate(exchange -> {
                    String path = exchange.getRequest().getPath().value();
                    return path.startsWith("/user/") || path.equals("/user") || path.startsWith("/booking/")
                            || path.equals("/booking") || path.startsWith("/bookings/") || path.equals("/bookings");
                }).uri("lb://BOOKING-SERVICE").build();

        return new RouteLocator() {
            @Override
            public Flux<Route> getRoutes() {
                return Flux.just(hotelRoute, roomsRoute, bookingRoute);
            }
        };
    }
}