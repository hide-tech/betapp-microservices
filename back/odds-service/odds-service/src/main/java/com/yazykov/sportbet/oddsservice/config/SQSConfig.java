package com.yazykov.sportbet.oddsservice.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SQSConfig {

    private final AWSParameters parameters;

    private AmazonSQSAsync amazonSQSAsync(){
        return AmazonSQSAsyncClientBuilder
                .standard()
                .withRegion(parameters.region())
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(parameters.accessKey(), parameters.secretKey())))
                .build();
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(){
        return new QueueMessagingTemplate(amazonSQSAsync());
    }
}
