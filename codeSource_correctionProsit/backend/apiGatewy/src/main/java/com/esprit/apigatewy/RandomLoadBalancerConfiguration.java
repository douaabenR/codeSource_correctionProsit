package com.esprit.apigatewy;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.CompletionContext;
import org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycle;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Random selection and instance logging for the configured load-balancer clients. */
public class RandomLoadBalancerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RandomLoadBalancerConfiguration.class);

    @Bean
    ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        String serviceId = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RandomLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(serviceId, ServiceInstanceListSupplier.class),
                serviceId);
    }

    @Bean
    LoadBalancerLifecycle<Object, Object, ServiceInstance> selectedInstanceLogger() {
        return new LoadBalancerLifecycle<>() {
            @Override
            public void onStart(Request<Object> request) {
            }

            @Override
            public void onStartRequest(Request<Object> request, Response<ServiceInstance> response) {
                if (response.hasServer()) {
                    ServiceInstance instance = response.getServer();
                    log.info("Random load balancer selected {} instance {}:{}",
                            instance.getServiceId(), instance.getHost(), instance.getPort());
                }
            }

            @Override
            public void onComplete(CompletionContext<Object, ServiceInstance, Object> context) {
            }
        };
    }
}
