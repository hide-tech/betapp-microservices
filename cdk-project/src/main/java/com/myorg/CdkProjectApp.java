package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.apigatewayv2.CfnIntegration;
import software.amazon.awscdk.services.apigatewayv2.CfnIntegrationProps;
import software.amazon.awscdk.services.apigatewayv2.CfnRoute;
import software.amazon.awscdk.services.apigatewayv2.CfnRouteProps;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApiProps;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.sqs.QueueProps;

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

        ScheduleServiceStack scheduleServiceStack = new ScheduleServiceStack(app, "bet-app-schedule-service", StackProps.builder()
                .env(environment)
                .build(), secret);

        OddsServiceStack oddsServiceStack = new OddsServiceStack(app, "bet-app-odds-service",
                StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret);
        String oddsQueueUrl = oddsServiceStack.oddsQueueUrl;

        CustomerServiceStack customerServiceStack = new CustomerServiceStack(app, "bet-app-сustomer-service",
                StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret);
        String customerQueueUrl = customerServiceStack.customerQueueUrl;

        PaymentServiceStack paymentServiceStack = new PaymentServiceStack(app, "bet-app-payment-service",
                StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret);
        String paymentQueueUrl = paymentServiceStack.paymentQueueUrl;

        ResultServiceStack resultServiceStack = new ResultServiceStack(app, "bet-app-result-service",
                StackProps.builder()
                .env(environment)
                .build(), orderQueueUrl, secret);
        String resultQueueUrl = resultServiceStack.resultQueueUrl;

        OrderServiceStack orderServiceStack = new OrderServiceStack(app, "bet-app-order-service",
                StackProps.builder()
                .env(environment)
                .build(),
                orderQueueUrl, oddsQueueUrl, customerQueueUrl, paymentQueueUrl, resultQueueUrl, secret);

        HttpApi api = new HttpApi(app, "HttpApiGateway", HttpApiProps.builder()
                .apiName("bet-api")
                .build());

        CfnIntegration scheduleIntegration = new CfnIntegration(app, "ApiGwScheduleServiceIntegration",
                CfnIntegrationProps.builder()
                        .apiId(api.getHttpApiId())
                        .connectionId(scheduleServiceStack.cfnResource.getRef())
                        .connectionType("VPC_LINK")
                        .description("Integration between ScheduleService and ApiGw")
                        .integrationMethod("ANY")
                        .integrationType("HTTP_PROXY")
                        .integrationUri(scheduleServiceStack.loadBalancer.getListener().getListenerArn())
                        .payloadFormatVersion("1.0")
                        .build());

        new CfnRoute(app, "ApiGwScheduleRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(scheduleIntegration.getRef())
                .build());

        CfnIntegration oddsIntegration = new CfnIntegration(app, "ApiGwOddsServiceIntegration",
                CfnIntegrationProps.builder()
                        .apiId(api.getHttpApiId())
                        .connectionId(oddsServiceStack.cfnResource.getRef())
                        .connectionType("VPC_LINK")
                        .description("Integration between OddsService and ApiGw")
                        .integrationMethod("ANY")
                        .integrationType("HTTP_PROXY")
                        .integrationUri(oddsServiceStack.loadBalancer.getListener().getListenerArn())
                        .payloadFormatVersion("1.0")
                        .build());

        new CfnRoute(app, "ApiGwOddsRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(oddsIntegration.getRef())
                .build());

        CfnIntegration customerIntegration = new CfnIntegration(app, "ApiGwCustomerServiceIntegration",
                CfnIntegrationProps.builder()
                        .apiId(api.getHttpApiId())
                        .connectionId(customerServiceStack.cfnResource.getRef())
                        .connectionType("VPC_LINK")
                        .description("Integration between CustomerService and ApiGw")
                        .integrationMethod("ANY")
                        .integrationType("HTTP_PROXY")
                        .integrationUri(customerServiceStack.loadBalancer.getListener().getListenerArn())
                        .payloadFormatVersion("1.0")
                        .build());

        new CfnRoute(app, "ApiGwCustomerRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(customerIntegration.getRef())
                .build());

        CfnIntegration paymentIntegration = new CfnIntegration(app, "ApiGwPaymentServiceIntegration",
                CfnIntegrationProps.builder()
                        .apiId(api.getHttpApiId())
                        .connectionId(paymentServiceStack.cfnResource.getRef())
                        .connectionType("VPC_LINK")
                        .description("Integration between PaymentService and ApiGw")
                        .integrationMethod("ANY")
                        .integrationType("HTTP_PROXY")
                        .integrationUri(paymentServiceStack.loadBalancer.getListener().getListenerArn())
                        .payloadFormatVersion("1.0")
                        .build());

        new CfnRoute(app, "ApiGwPaymentRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(paymentIntegration.getRef())
                .build());

        CfnIntegration resultIntegration = new CfnIntegration(app, "ApiGwResultServiceIntegration",
                CfnIntegrationProps.builder()
                        .apiId(api.getHttpApiId())
                        .connectionId(resultServiceStack.cfnResource.getRef())
                        .connectionType("VPC_LINK")
                        .description("Integration between ResultService and ApiGw")
                        .integrationMethod("ANY")
                        .integrationType("HTTP_PROXY")
                        .integrationUri(resultServiceStack.loadBalancer.getListener().getListenerArn())
                        .payloadFormatVersion("1.0")
                        .build());

        new CfnRoute(app, "ApiGwResultRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(resultIntegration.getRef())
                .build());

        CfnIntegration orderIntegration = new CfnIntegration(app, "ApiGwOrderServiceIntegration",
                CfnIntegrationProps.builder()
                        .apiId(api.getHttpApiId())
                        .connectionId(orderServiceStack.cfnResource.getRef())
                        .connectionType("VPC_LINK")
                        .description("Integration between OrderService and ApiGw")
                        .integrationMethod("ANY")
                        .integrationType("HTTP_PROXY")
                        .integrationUri(orderServiceStack.loadBalancer.getListener().getListenerArn())
                        .payloadFormatVersion("1.0")
                        .build());

        new CfnRoute(app, "ApiGwOrderRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(orderIntegration.getRef())
                .build());

        new CfnOutput(app, "ApiGwUrl", CfnOutputProps.builder()
                .value(api.getUrl())
                .build());

        app.synth();
    }
}

