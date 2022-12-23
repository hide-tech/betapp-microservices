package com.yazykov.sportbet.paymentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud")
public record AWSParameters(String accessKey,
                            String secretKey,
                            String region,
                            String dynamoUrl,
                            String distinctQueue,
                            String receiveQueue) {
}
