package com.esprit.apigatewy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@SpringBootApplication
@EnableDiscoveryClient
@LoadBalancerClient(name = "candidat", configuration = RandomLoadBalancerConfiguration.class)
public class ApiGatewyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewyApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder, Environment environment) {
        RouteLocatorBuilder.Builder routes = builder.routes();

        // The properties-routes profile uses the equivalent declarations in application.properties.
        if (!environment.acceptsProfiles(Profiles.of("properties-routes"))) {
            routes.route("Candidature", r -> r
                            .path("/candidatures/**")
                            .uri("lb://candidature"))

                    .route("Job", r -> r
                            .path("/jobs/**")
                            .filters(f -> f.rewritePath("/jobs(?<remaining>/?.*)", "/api/jobs${remaining}"))
                            .uri("lb://job"));
        }

        return routes.route("Candidat", r -> r
                            .path("/candidat/**", "/candidats/**", "/candidates/**")
                            .filters(f -> f.rewritePath("/candidat(e)?s?(?<remaining>/?.*)", "/mic1/candidates${remaining}"))
                            .uri("lb://candidat"))

                .build();
    }
}
