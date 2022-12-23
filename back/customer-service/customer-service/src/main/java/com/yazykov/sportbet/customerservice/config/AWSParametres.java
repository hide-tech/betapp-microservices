package com.yazykov.sportbet.customerservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud")
public record AWSParametres(String accessKey,
                            String secretKey,
                            String region,
                            String receiveQueue,
                            String distinctQueue) {
}
