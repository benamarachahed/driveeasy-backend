package com.driveeasy.locationservice.config;

import com.driveeasy.locationservice.client.ClientClient;
import com.driveeasy.locationservice.client.VehiculeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    @Autowired
    private LoadBalancedExchangeFilterFunction filterFunction;

    @Bean
    public WebClient vehiculeWebClient() {
        return WebClient.builder()
                .baseUrl("http://vehicule-service")
                .filter(filterFunction)
                .build();
    }

    @Bean
    public WebClient clientWebClient() {
        return WebClient.builder()
                .baseUrl("http://client-service")
                .filter(filterFunction)
                .build();
    }

    @Bean
    public VehiculeClient vehiculeClient() {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(vehiculeWebClient()))
                .build();
        return factory.createClient(VehiculeClient.class);
    }

    @Bean
    public ClientClient clientClient() {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(clientWebClient()))
                .build();
        return factory.createClient(ClientClient.class);
    }
}