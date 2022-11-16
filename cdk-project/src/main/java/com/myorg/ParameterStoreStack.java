package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.amazon.awscdk.services.ssm.StringParameterProps;
import software.constructs.Construct;

public class ParameterStoreStack extends Stack {

    public ParameterStoreStack(final Construct scope, final String id) {
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

    public ParameterStoreStack(final Construct scope,
                           final String id,
                           final StackProps props,
                           final ScheduleServiceStack scheduleServiceStack,
                           final OddsServiceStack oddsServiceStack,
                           final CustomerServiceStack customerServiceStack,
                           final PaymentServiceStack paymentServiceStack,
                           final ResultServiceStack resultServiceStack,
                           final OrderServiceStack orderServiceStack) {
        super(scope, id, props);

        //schedule-service env params
        new StringParameter(this, "schedul-service-db-username", StringParameterProps.builder()
                .parameterName("/config/schedule-service/spring.datasource.username")
                .stringValue(scheduleServiceStack.scheduleDatabaseName)
                .build());

        new StringParameter(this, "schedul-service-db-password", StringParameterProps.builder()
                .parameterName("/config/schedule-service/spring.datasource.password")
                .stringValue(scheduleServiceStack.schedulePostgresPass)
                .build());

        new StringParameter(this, "schedul-service-db-url", StringParameterProps.builder()
                .parameterName("/config/schedule-service/spring.datasource.url")
                .stringValue(scheduleServiceStack.scheduleServiceDbUrl)
                .build());

        new StringParameter(this, "schedul-service-flyway-username", StringParameterProps.builder()
                .parameterName("/config/schedule-service/spring.datasource.flyway.user")
                .stringValue(scheduleServiceStack.scheduleDatabaseName)
                .build());

        new StringParameter(this, "schedul-service-flyway-password", StringParameterProps.builder()
                .parameterName("/config/schedule-service/spring.datasource.flyway.password")
                .stringValue(scheduleServiceStack.schedulePostgresPass)
                .build());

        new StringParameter(this, "schedul-service-flyway-url", StringParameterProps.builder()
                .parameterName("/config/schedule-service/spring.datasource.flyway.url")
                .stringValue(scheduleServiceStack.scheduleServiceDbUrl)
                .build());

        //odds-service env params
        new StringParameter(this, "odd-service-db-username", StringParameterProps.builder()
                .parameterName("/config/odds-service/spring.datasource.username")
                .stringValue(oddsServiceStack.oddsPostgresUsername)
                .build());

        new StringParameter(this, "odd-service-db-password", StringParameterProps.builder()
                .parameterName("/config/odds-service/spring.datasource.password")
                .stringValue(oddsServiceStack.oddsPostgresPass)
                .build());

        new StringParameter(this, "odd-service-db-url", StringParameterProps.builder()
                .parameterName("/config/odds-service/spring.datasource.url")
                .stringValue(oddsServiceStack.oddsServiceDBUrl)
                .build());

        new StringParameter(this, "odd-service-flyway-username", StringParameterProps.builder()
                .parameterName("/config/odds-service/spring.datasource.flyway.user")
                .stringValue(oddsServiceStack.oddsPostgresUsername)
                .build());

        new StringParameter(this, "odd-service-flyway-password", StringParameterProps.builder()
                .parameterName("/config/odds-service/spring.datasource.flyway.password")
                .stringValue(oddsServiceStack.oddsPostgresPass)
                .build());

        new StringParameter(this, "odd-service-flyway-url", StringParameterProps.builder()
                .parameterName("/config/odds-service/spring.datasource.flyway.url")
                .stringValue(oddsServiceStack.oddsServiceDBUrl)
                .build());

        new StringParameter(this, "odd-service-sqs-send", StringParameterProps.builder()
                .parameterName("/config/odds-service/cloud.aws.end-point.url-send")
                .stringValue(scheduleServiceStack.orderQueueUrl)
                .build());

        new StringParameter(this, "odd-service-sqs-receive", StringParameterProps.builder()
                .parameterName("/config/odds-service/cloud.aws.end-point.url-receive")
                .stringValue(oddsServiceStack.oddsQueueUrl)
                .build());

        //customer-service env params
        new StringParameter(this, "custom-service-db-username", StringParameterProps.builder()
                .parameterName("/config/customer-service/spring.datasource.username")
                .stringValue(customerServiceStack.customerPostgresUsername)
                .build());

        new StringParameter(this, "custom-service-db-password", StringParameterProps.builder()
                .parameterName("/config/customer-service/spring.datasource.password")
                .stringValue(customerServiceStack.customerPostgresPass)
                .build());

        new StringParameter(this, "custom-service-db-url", StringParameterProps.builder()
                .parameterName("/config/customer-service/spring.datasource.url")
                .stringValue(customerServiceStack.customerServiceDbUrl)
                .build());

        new StringParameter(this, "custom-service-flyway-username", StringParameterProps.builder()
                .parameterName("/config/customer-service/spring.datasource.flyway.user")
                .stringValue(customerServiceStack.customerPostgresUsername)
                .build());

        new StringParameter(this, "custom-service-flyway-password", StringParameterProps.builder()
                .parameterName("/config/customer-service/spring.datasource.flyway.password")
                .stringValue(customerServiceStack.customerPostgresPass)
                .build());

        new StringParameter(this, "custom-service-flyway-url", StringParameterProps.builder()
                .parameterName("/config/customer-service/spring.datasource.flyway.url")
                .stringValue(customerServiceStack.customerServiceDbUrl)
                .build());

        new StringParameter(this, "custom-service-sqs-send", StringParameterProps.builder()
                .parameterName("/config/customer-service/cloud.aws.end-point.url-send")
                .stringValue(scheduleServiceStack.orderQueueUrl)
                .build());

        new StringParameter(this, "custom-service-sqs-receive", StringParameterProps.builder()
                .parameterName("/config/customer-service/cloud.aws.end-point.url-receive")
                .stringValue(customerServiceStack.customerQueueUrl)
                .build());

        //payment-service env params
        new StringParameter(this, "pay-service-dynamo-table", StringParameterProps.builder()
                .parameterName("/config/payment-service/cloud.aws.dynamo.endpoint")
                .stringValue(paymentServiceStack.dynamoTableEndpoint)
                .build());

        new StringParameter(this, "pay-service-sqs-send", StringParameterProps.builder()
                .parameterName("/config/payment-service/cloud.aws.end-point.url-send")
                .stringValue(scheduleServiceStack.orderQueueUrl)
                .build());

        new StringParameter(this, "pay-service-sqs-receive", StringParameterProps.builder()
                .parameterName("/config/payment-service/cloud.aws.end-point.url-receive")
                .stringValue(paymentServiceStack.paymentQueueUrl)
                .build());

        //result-service env params
        new StringParameter(this, "res-service-db-username", StringParameterProps.builder()
                .parameterName("/config/result-service/spring.datasource.username")
                .stringValue(resultServiceStack.resultPostgresUsername)
                .build());

        new StringParameter(this, "res-service-db-password", StringParameterProps.builder()
                .parameterName("/config/result-service/spring.datasource.password")
                .stringValue(resultServiceStack.resultPostgresPass)
                .build());

        new StringParameter(this, "res-service-db-url", StringParameterProps.builder()
                .parameterName("/config/result-service/spring.datasource.url")
                .stringValue(resultServiceStack.resultServiceDbUrl)
                .build());

        new StringParameter(this, "res-service-flyway-username", StringParameterProps.builder()
                .parameterName("/config/result-service/spring.datasource.flyway.user")
                .stringValue(resultServiceStack.resultPostgresUsername)
                .build());

        new StringParameter(this, "res-service-flyway-password", StringParameterProps.builder()
                .parameterName("/config/result-service/spring.datasource.flyway.password")
                .stringValue(resultServiceStack.resultPostgresPass)
                .build());

        new StringParameter(this, "res-service-flyway-url", StringParameterProps.builder()
                .parameterName("/config/result-service/spring.datasource.flyway.url")
                .stringValue(resultServiceStack.resultServiceDbUrl)
                .build());

        new StringParameter(this, "res-service-sqs-send", StringParameterProps.builder()
                .parameterName("/config/result-service/cloud.aws.end-point.url-send")
                .stringValue(scheduleServiceStack.orderQueueUrl)
                .build());

        new StringParameter(this, "res-service-sqs-receive", StringParameterProps.builder()
                .parameterName("/config/result-service/cloud.aws.end-point.url-receive")
                .stringValue(resultServiceStack.resultQueueUrl)
                .build());

        //order-service env params
        new StringParameter(this, "order-service-db-username", StringParameterProps.builder()
                .parameterName("/config/result-service/spring.datasource.username")
                .stringValue(orderServiceStack.orderPostgresUsername)
                .build());

        new StringParameter(this, "order-service-db-password", StringParameterProps.builder()
                .parameterName("/config/order-service/spring.datasource.password")
                .stringValue(orderServiceStack.orderPostgresPass)
                .build());

        new StringParameter(this, "order-service-db-url", StringParameterProps.builder()
                .parameterName("/config/order-service/spring.datasource.url")
                .stringValue(orderServiceStack.orderServiceDbUrl)
                .build());

        new StringParameter(this, "order-service-flyway-username", StringParameterProps.builder()
                .parameterName("/config/order-service/spring.datasource.flyway.user")
                .stringValue(orderServiceStack.orderPostgresUsername)
                .build());

        new StringParameter(this, "order-service-flyway-password", StringParameterProps.builder()
                .parameterName("/config/order-service/spring.datasource.flyway.password")
                .stringValue(orderServiceStack.orderPostgresPass)
                .build());

        new StringParameter(this, "order-service-flyway-url", StringParameterProps.builder()
                .parameterName("/config/order-service/spring.datasource.flyway.url")
                .stringValue(orderServiceStack.orderServiceDbUrl)
                .build());

        new StringParameter(this, "order-service-sqs-receive", StringParameterProps.builder()
                .parameterName("/config/order-service/cloud.aws.end-points.url-receive")
                .stringValue(scheduleServiceStack.orderQueueUrl)
                .build());

        new StringParameter(this, "order-service-sqs-send-odds", StringParameterProps.builder()
                .parameterName("/config/order-service/cloud.aws.end-points.url-send-odd")
                .stringValue(oddsServiceStack.oddsQueueUrl)
                .build());

        new StringParameter(this, "order-service-sqs-send-customer", StringParameterProps.builder()
                .parameterName("/config/order-service/cloud.aws.end-points.url-send-customer")
                .stringValue(customerServiceStack.customerQueueUrl)
                .build());

        new StringParameter(this, "order-service-sqs-send-payment", StringParameterProps.builder()
                .parameterName("/config/order-service/cloud.aws.end-points.url-send-payment")
                .stringValue(paymentServiceStack.paymentQueueUrl)
                .build());

        new StringParameter(this, "order-service-sqs-send-result", StringParameterProps.builder()
                .parameterName("/config/order-service/cloud.aws.end-points.url-send-result")
                .stringValue(resultServiceStack.resultQueueUrl)
                .build());
    }
}
