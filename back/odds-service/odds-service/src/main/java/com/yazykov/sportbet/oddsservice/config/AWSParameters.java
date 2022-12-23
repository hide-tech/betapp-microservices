package com.yazykov.sportbet.oddsservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud")
public record AWSParameters(String secretKey,
                            String accessKey,
                            String region,
                            String distinctQueue,
                            String receiveQueue) {
}
