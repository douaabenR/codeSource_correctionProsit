package com.esprit.apigatewy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@LoadBalancerClient(name = "candidat", configuration = RandomLoadBalancerConfiguration.class)
public class ApiGatewyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewyApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("Candidature", r -> r
                        .path("/candidatures/**")
                        .uri("lb://candidature"))

                .route("Job", r -> r
                        .path("/jobs/**")
                        .filters(f -> f.rewritePath("/jobs(?<remaining>/?.*)", "/api/jobs${remaining}"))
                        .uri("lb://job"))

                .route("Candidat", r -> r
                        .path("/candidat/**", "/candidats/**")
                        .filters(f -> f.rewritePath("/candidats?(?<remaining>/?.*)", "/mic1/candidates${remaining}"))
                        .uri("lb://candidat"))

                .build();
    }
}
