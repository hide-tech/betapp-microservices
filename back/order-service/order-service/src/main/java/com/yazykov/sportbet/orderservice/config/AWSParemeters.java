package com.yazykov.sportbet.orderservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud")
public record AWSParemeters(String accessKey,
                            String secretKey,
                            String region,
                            String customerQueue,
                            String oddQueue,
                            String paymentQueue,
                            String resultQueue,
                            String receiveQueue) {
}
