package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class CdkProjectApp {
    public static void main(final String[] args) {
        App app = new App();

        String accessId = (String) app.getNode().tryGetContext("accessId");
        String secret = (String) app.getNode().tryGetContext("secret");
        String region = (String) app.getNode().tryGetContext("region");

        new ScheduleServiceStack(app, "bet-app-schedule-service", StackProps.builder()
                .env(Environment.builder().account(accessId).region(region).build())
                .build());

        new OddsServiceStack(app, "bet-app-odds-service", StackProps.builder()
                .env(Environment.builder().account(accessId).region(region).build())
                .build());

        new CustomerServiceStack(app, "bet-app-ustomer-service", StackProps.builder()
                .env(Environment.builder().account(accessId).region(region).build())
                .build());

        new PaymentServiceStack(app, "bet-app-payment-service", StackProps.builder()
                .env(Environment.builder().account(accessId).region(region).build())
                .build());

        new ResultServiceStack(app, "bet-app-result-service", StackProps.builder()
                .env(Environment.builder().account(accessId).region(region).build())
                .build());

        new OrderServiceStack(app, "bet-app-order-service", StackProps.builder()
                .env(Environment.builder().account(accessId).region(region).build())
                .build());

        app.synth();
    }
}

