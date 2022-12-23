package com.yazykov.sportbet.resultservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud")
public record AWSParameters(String accessKey,
                            String secretKey,
                            String region,
                            String distinctQueue,
                            String receiveQueue) {
}
