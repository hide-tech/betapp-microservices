package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class CdkProjectApp {
    public static void main(final String[] args) {
        App app = new App();

        String accessId = (String) app.getNode().tryGetContext("accessId");
        String secret = (String) app.getNode().tryGetContext("secret");
        String region = (String) app.getNode().tryGetContext("region");
        String account = (String) app.getNode().tryGetContext("account");

        Environment environment = Environment.builder()
                .account(account)
                .region(region)
                .build();

        ScheduleServiceStack scheduleServiceStack = new ScheduleServiceStack(app, "bet-app-schedule-service",
                StackProps.builder()
                .env(environment)
                .build(), secret);
        String orderQueueUrl = scheduleServiceStack.orderQueueUrl;

        OddsServiceStack oddsServiceStack = new OddsServiceStack(app, "bet-app-odds-service",
                StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret, accessId);
        String oddsQueueUrl = oddsServiceStack.oddsQueueUrl;

        CustomerServiceStack customerServiceStack = new CustomerServiceStack(app, "bet-app-customer-service",
                StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret, accessId);
        String customerQueueUrl = customerServiceStack.customerQueueUrl;

        PaymentServiceStack paymentServiceStack = new PaymentServiceStack(app, "bet-app-payment-service",
                StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret, accessId);
        String paymentQueueUrl = paymentServiceStack.paymentQueueUrl;

        ResultServiceStack resultServiceStack = new ResultServiceStack(app, "bet-app-result-service",
                StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret, accessId);
        String resultQueueUrl = resultServiceStack.resultQueueUrl;

        OrderServiceStack orderServiceStack = new OrderServiceStack(app, "bet-app-order-service",
                StackProps.builder()
                .env(environment)
                .build(),
                orderQueueUrl, oddsQueueUrl, customerQueueUrl, paymentQueueUrl, resultQueueUrl, secret, accessId);

        new ParameterStoreStack(app, "bet-app-param-store",
                StackProps.builder().env(environment).build(),
                scheduleServiceStack,
                oddsServiceStack,
                customerServiceStack,
                paymentServiceStack,
                resultServiceStack,
                orderServiceStack);

        new ApiGatewayStack(app,
                "api-gateway-stack",
                StackProps.builder().env(environment).build(),
                scheduleServiceStack,
                oddsServiceStack,
                customerServiceStack,
                paymentServiceStack,
                resultServiceStack,
                orderServiceStack);

        app.synth();
    }
}

