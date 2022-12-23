package com.yazykov.sportbet.paymentservice.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DynamoDBConfig {

    private final AWSParameters parameters;

    public AmazonDynamoDB amazonDynamoDB(){
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration(parameters.dynamoUrl(), parameters.region()))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(parameters.accessKey(), parameters.secretKey())))
                .build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(){
        return new DynamoDBMapper(amazonDynamoDB());
    }
}
