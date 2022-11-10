package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.sqs.QueueProps;

import java.util.Arrays;

public class CdkProjectApp {
    public static void main(final String[] args) {
        App app = new App();

        String accessId = (String) app.getNode().tryGetContext("accessId");
        String secret = (String) app.getNode().tryGetContext("secret");
        String region = (String) app.getNode().tryGetContext("region");

        Environment environment = Environment.builder()
                .account(accessId)
                .region(region)
                .build();

        Queue orderQueue = new Queue(app, "orderQueue", QueueProps.builder()
                .queueName("OrderQueue")
                .contentBasedDeduplication(true)
                .fifo(true)
                .build());
        String orderQueueUrl = orderQueue.getQueueUrl();

        new ScheduleServiceStack(app, "bet-app-schedule-service", StackProps.builder()
                .env(environment)
                .build(), secret);

        OddsServiceStack oddsServiceStack = new OddsServiceStack(app, "bet-app-odds-service", StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret);
        String oddsQueueUrl = oddsServiceStack.oddsQueueUrl;

        CustomerServiceStack customerServiceStack = new CustomerServiceStack(app, "bet-app-сustomer-service", StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret);
        String customerQueueUrl = customerServiceStack.customerQueueUrl;

        PaymentServiceStack paymentServiceStack = new PaymentServiceStack(app, "bet-app-payment-service", StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret);
        String paymentQueueUrl = paymentServiceStack.paymentQueueUrl;

        ResultServiceStack resultServiceStack = new ResultServiceStack(app, "bet-app-result-service", StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret);
        String resultQueueUrl = resultServiceStack.resultQueueUrl;

        new OrderServiceStack(app, "bet-app-order-service", StackProps.builder()
                .env(environment)
                .build());

        app.synth();
    }
}

