package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.CfnResource;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateServiceProps;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.rds.*;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderServiceStack extends Stack {

    public final String orderPostgresUsername = "postgresUser";
    public final String orderPostgresPass = "postgresPass";
    public final String orderDatabaseName = "orders";
    public CfnResource cfnResource;
    public ApplicationLoadBalancedFargateService loadBalancer;
    public String orderServiceDbUrl;

    public OrderServiceStack(final Construct scope, final String id) {
        this(scope, id, null, null, null, null, null, null, null, null);
    }

    public OrderServiceStack(final Construct scope,
                             final String id,
                             final StackProps props,
                             final String orderQueueUrl,
                             final String oddsQueueUrl,
                             final String customerQueueUrl,
                             final String paymentQueueUrl,
                             final String resultQueueUrl,
                             final String secret,
                             final String accessId) {
        super(scope, id, props);

        Vpc vpc = new Vpc(this, "order-service-vpc", VpcProps.builder()
                .maxAzs(2)
                .natGateways(1)
                .build());

        DatabaseInstance orderDb = new DatabaseInstance(this, "order-database",
                DatabaseInstanceProps.builder()
                        .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_13_3)
                                .build()))
                        .instanceType(InstanceType.of(InstanceClass.T3, InstanceSize.MICRO))
                        .vpc(vpc)
                        .availabilityZone(vpc.getAvailabilityZones().get(0))
                        .allocatedStorage(200)
                        .multiAz(false)
                        .databaseName(orderDatabaseName)
                        .credentials(Credentials.fromPassword(orderPostgresUsername, SecretValue.unsafePlainText(orderPostgresPass)))
                        .build());

        String dbInstanceSocketAddress = orderDb.getInstanceEndpoint().getSocketAddress();
        String dbConnectUri = "jdbc:postgresql://" + dbInstanceSocketAddress + "/" + orderDatabaseName;
        orderServiceDbUrl = dbConnectUri;

        Cluster cluster = new Cluster(this, "order-service-cluster", ClusterProps.builder()
                .clusterName("orderServiceCluster")
                .vpc(vpc)
                .build());

        Map<String, String> containerEnv = new HashMap<>();
        containerEnv.put("CLOUD_AWS_CREDENTIALS_ACCESS-KEY", accessId);
        containerEnv.put("CLOUD_AWS_CREDENTIALS_SECRET-KEY", secret);
        containerEnv.put("CLOUD_AWS_REGION_STATIC", props.getEnv().getRegion());

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

        loadBalancer = serviceApp;

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

        Map<String, Object> cfnProps = new HashMap<>();
        cfnProps.put("Name", "V2 vpc link order-service");
        cfnProps.put("SubnetIds", vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId).collect(Collectors.toList()));

        CfnResource httpVpcLink = new software.amazon.awscdk.CfnResource(this, "HttpVpcLinkOrder", CfnResourceProps.builder()
                .type("AWS::ApiGatewayV2::VpcLink")
                .properties(cfnProps)
                .build());
        cfnResource = httpVpcLink;
    }
}
