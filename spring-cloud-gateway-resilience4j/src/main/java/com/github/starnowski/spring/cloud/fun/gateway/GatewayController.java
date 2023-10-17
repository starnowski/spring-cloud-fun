package com.github.starnowski.spring.cloud.fun.gateway;

import com.github.starnowski.spring.cloud.fun.gateway.model.User;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static io.github.resilience4j.core.SupplierUtils.recover;

@RestController
public class GatewayController {

    private CircuitBreakerRegistry circuitBreakerRegistry;
    private CircuitBreaker circuitBreaker;

    protected void postConstructor() {
        // Create a custom configuration for a CircuitBreaker
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .permittedNumberOfCallsInHalfOpenState(2)
                .slidingWindowSize(2)
                .recordExceptions(IOException.class, TimeoutException.class)
//                .ignoreExceptions(BusinessException.class, OtherBusinessException.class)
                .build();

        circuitBreakerRegistry =
                CircuitBreakerRegistry.of(circuitBreakerConfig);
        circuitBreaker = circuitBreakerRegistry
                .circuitBreaker("service1");
    }

    @Value("${remote.home}")
    private URI home;

    @GetMapping("/test")
    public ResponseEntity<?> proxy(ProxyExchange<Object> proxy) throws Exception {
        return proxy.uri(home.toString() + "/test.json").get();
    }

    @GetMapping("/test-with-circuit-breaker")
    public ResponseEntity<?> proxyWithCircuitBreaker(ProxyExchange<Object> proxy) throws Exception {
        Supplier<ResponseEntity<?>> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> proxy.uri(home.toString() + "/service1").get());

        ResponseEntity<User> fallback = new ResponseEntity<User>(new User(-1, -1, "Dummy user"), HttpStatusCode.valueOf(200));
        return recover(decoratedSupplier, throwable -> fallback).get();
//        return proxy.uri(home.toString() + "/service1").get();
    }
}
