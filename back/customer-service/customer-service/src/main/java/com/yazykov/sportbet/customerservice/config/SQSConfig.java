package com.yazykov.sportbet.customerservice.config;

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

    private final AWSParametres parametres;

    private AmazonSQSAsync amazonSQSAsync(){
        return AmazonSQSAsyncClientBuilder
                .standard()
                .withRegion(parametres.region())
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(parametres.accessKey(), parametres.secretKey())))
                .build();
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(){
        return new QueueMessagingTemplate(amazonSQSAsync());
    }
}
