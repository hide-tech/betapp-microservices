package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigatewayv2.CfnIntegration;
import software.amazon.awscdk.services.apigatewayv2.CfnIntegrationProps;
import software.amazon.awscdk.services.apigatewayv2.CfnRoute;
import software.amazon.awscdk.services.apigatewayv2.CfnRouteProps;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApiProps;
import software.constructs.Construct;

public class ApiGatewayStack extends Stack {

    public ApiGatewayStack(final Construct scope, final String id) {
        this(scope,
                id,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public ApiGatewayStack(final Construct scope,
                           final String id,
                           final StackProps props,
                           final ScheduleServiceStack scheduleServiceStack,
                           final OddsServiceStack oddsServiceStack,
                           final CustomerServiceStack customerServiceStack,
                           final PaymentServiceStack paymentServiceStack,
                           final ResultServiceStack resultServiceStack,
                           final OrderServiceStack orderServiceStack) {
        super(scope, id, props);

        HttpApi api = new HttpApi(this, "HttpApiGateway", HttpApiProps.builder()
                .apiName("bet-api")
                .build());

        CfnIntegration scheduleIntegration = new CfnIntegration(this, "ApiGwScheduleServiceIntegration",
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

        new CfnRoute(this, "ApiGwScheduleRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(scheduleIntegration.getRef())
                .build());

        CfnIntegration oddsIntegration = new CfnIntegration(this, "ApiGwOddsServiceIntegration",
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

        new CfnRoute(this, "ApiGwOddsRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(oddsIntegration.getRef())
                .build());

        CfnIntegration customerIntegration = new CfnIntegration(this, "ApiGwCustomerServiceIntegration",
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

        new CfnRoute(this, "ApiGwCustomerRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(customerIntegration.getRef())
                .build());

        CfnIntegration paymentIntegration = new CfnIntegration(this, "ApiGwPaymentServiceIntegration",
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

        new CfnRoute(this, "ApiGwPaymentRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(paymentIntegration.getRef())
                .build());

        CfnIntegration resultIntegration = new CfnIntegration(this, "ApiGwResultServiceIntegration",
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

        new CfnRoute(this, "ApiGwResultRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(resultIntegration.getRef())
                .build());

        CfnIntegration orderIntegration = new CfnIntegration(this, "ApiGwOrderServiceIntegration",
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

        new CfnRoute(this, "ApiGwOrderRoute", CfnRouteProps.builder()
                .apiId(api.getHttpApiId())
                .routeKey("ANY /")
                .target(orderIntegration.getRef())
                .build());

        new CfnOutput(this, "ApiGwUrl", CfnOutputProps.builder()
                .value(api.getUrl())
                .build());
    }
}
