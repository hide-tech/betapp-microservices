package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.SecretValue;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateServiceProps;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.rds.*;
import software.constructs.Construct;

import java.util.Map;

public class OrderServiceStack extends Stack {

    private final String orderPostgresUsername = "postgresUser";
    private final String orderPostgresPass = "postgresPass";
    private final String orderDatabaseName = "orders";

    public OrderServiceStack(final Construct scope, final String id) {
        this(scope, id, null, null, null, null, null, null, null);
    }

    public OrderServiceStack(final Construct scope,
                             final String id,
                             final StackProps props,
                             final String orderQueueUrl,
                             final String oddsQueueUrl,
                             final String customerQueueUrl,
                             final String paymentQueueUrl,
                             final String resultQueueUrl,
                             final String secret) {
        super(scope, id, props);

        Vpc vpc = new Vpc(this, "order-service-vpc", VpcProps.builder()
                .maxAzs(1)
                .natGateways(1)
                .build());

        DatabaseInstance orderDb = new DatabaseInstance(this, "order-database",
                DatabaseInstanceProps.builder()
                        .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_13_7)
                                .build()))
                        .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                        .vpc(vpc)
                        .allocatedStorage(200)
                        .databaseName(orderDatabaseName)
                        .credentials(Credentials.fromPassword(orderPostgresUsername, SecretValue.plainText(orderPostgresPass)))
                        .build());

        String dbInstanceSocketAddress = orderDb.getInstanceEndpoint().getSocketAddress();
        String dbConnectUri = "jdbc:postgresql://" + dbInstanceSocketAddress + "/" + orderDatabaseName;

        Cluster cluster = new Cluster(this, "order-service-cluster", ClusterProps.builder()
                .clusterName("orderServiceCluster")
                .vpc(vpc)
                .build());

        Map<String, String> containerEnv = Map.of("SPRING_DATASOURCE_URL",dbConnectUri,
                "SPRING_DATASOURCE_USERNAME", orderPostgresUsername,
                "SPRING_DATASOURCE_PASSWORD", orderPostgresPass,
                "SPRING_DATASOURCE_FLYWAY_URL", dbConnectUri,
                "SPRING_DATASOURCE_FLYWAY_USER", orderPostgresUsername,
                "SPRING_DATASOURCE_FLYWAY_PASSWORD", orderPostgresPass,
                "CLOUD_AWS_CREDENTIALS_ACCESS-KEY", props.getEnv().getAccount(),
                "CLOUD_AWS_CREDENTIALS_SECRET-KEY", secret,
                "CLOUD_AWS_REGION_STATIC", props.getEnv().getRegion(),
                "CLOUD_AWS_END-POINTS_URL-RECEIVE", orderQueueUrl);
        containerEnv.put("CLOUD_AWS_END-POINTS_URL-SEND-CUSTOMER", customerQueueUrl);
        containerEnv.put("CLOUD_AWS_END-POINTS_URL-SEND-ODD", oddsQueueUrl);
        containerEnv.put("CLOUD_AWS_END-POINTS_URL-SEND-PAYMENT", paymentQueueUrl);
        containerEnv.put("CLOUD_AWS_END-POINTS_URL-SEND-RESULT", resultQueueUrl);

        ApplicationLoadBalancedFargateService serviceApp = new ApplicationLoadBalancedFargateService(
                this, "order-service-load-balancer", ApplicationLoadBalancedFargateServiceProps.builder()
                .cluster(cluster)
                .desiredCount(2)
                .cpu(256)
                .memoryLimitMiB(512)
                .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                        .image(ContainerImage.fromAsset("../back/order-service/order-service",
                                AssetImageProps.builder()
                                        .buildArgs(containerEnv)
                                        .build()))
                        .containerPort(8080)
                        .build())
                .build()
        );

        serviceApp.getTargetGroup().configureHealthCheck(HealthCheck.builder()
                .port("traffic-port")
                .path("/actuator/health")
                .interval(Duration.seconds(5))
                .timeout(Duration.seconds(4))
                .healthyThresholdCount(2)
                .unhealthyThresholdCount(2)
                .healthyHttpCodes("200")
                .build());

        ScalableTaskCount scalableTaskCount = serviceApp.getService().autoScaleTaskCount(EnableScalingProps.builder()
                .maxCapacity(4)
                .minCapacity(2)
                .build());

        scalableTaskCount.scaleOnCpuUtilization("cpu-autoscaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(45)
                .policyName("cpu-autoscaling-policy")
                .scaleInCooldown(Duration.seconds(30))
                .scaleOutCooldown(Duration.seconds(30))
                .build());
    }
}
