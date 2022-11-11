package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateServiceProps;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.sqs.QueueProps;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

public class ScheduleServiceStack extends Stack {

    private final String schedulePostgresUsername = "postgresUser";
    private final String schedulePostgresPass = "postgresPass";
    private final String scheduleDatabaseName = "schedules";
    public String orderQueueUrl;
    public CfnResource cfnResource;
    public ApplicationLoadBalancedFargateService loadBalancer;

    public ScheduleServiceStack(final Construct scope, final String id) {
        this(scope, id, null, null);
    }

    public ScheduleServiceStack(final Construct scope,
                                final String id,
                                final StackProps props,
                                final String secretKey) {
        super(scope, id, props);

        Vpc vpc = new Vpc(this, "schedule-service-vpc", VpcProps.builder()
                .maxAzs(1)
                .natGateways(1)
                .build());

        DatabaseInstance scheduleDB = new DatabaseInstance(this, "schedule-database",
                DatabaseInstanceProps.builder()
                        .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_13_7)
                                .build()))
                        .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                        .vpc(vpc)
                        .allocatedStorage(200)
                        .databaseName(scheduleDatabaseName)
                        .credentials(Credentials.fromPassword(schedulePostgresUsername, SecretValue.unsafePlainText(schedulePostgresPass)))
                        .build());

        String dbInstanceSocketAddress = scheduleDB.getInstanceEndpoint().getSocketAddress();
        String dbConnectUri = "jdbc:postgresql://" + dbInstanceSocketAddress + "/" + scheduleDatabaseName;

        Cluster cluster = new Cluster(this, "schedule-service-cluster", ClusterProps.builder()
                .clusterName("scheduleServiceCluster")
                .vpc(vpc)
                .build());

        Map<String, String> containerEnv = new HashMap<>();
        containerEnv.put("SPRING_DATASOURCE_URL",dbConnectUri);
        containerEnv.put("SPRING_DATASOURCE_USERNAME", schedulePostgresUsername);
        containerEnv.put("SPRING_DATASOURCE_PASSWORD", schedulePostgresPass);
        containerEnv.put("SPRING_DATASOURCE_FLYWAY_URL", dbConnectUri);
        containerEnv.put("SPRING_DATASOURCE_FLYWAY_USER", schedulePostgresUsername);
        containerEnv.put("SPRING_DATASOURCE_FLYWAY_PASSWORD", schedulePostgresPass);

        ApplicationLoadBalancedFargateService serviceApp = new ApplicationLoadBalancedFargateService(
                this, "schedule-service-load-balancer", ApplicationLoadBalancedFargateServiceProps.builder()
                .cluster(cluster)
                .desiredCount(2)
                .cpu(256)
                .memoryLimitMiB(512)
                .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                        .image(ContainerImage.fromAsset("../back/schedule-service/schedule-service",
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
        cfnProps.put("Name", "V2 vpc link schedule-service");
        cfnProps.put("SubnetIds", vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId));

        CfnResource httpVpcLink = new CfnResource(this, "HttpVpcLinkSchedule", CfnResourceProps.builder()
                .type("AWS::ApiGatewayV2::VpcLink")
                .properties(cfnProps)
                .build());
        cfnResource = httpVpcLink;

        Queue orderQueue = new Queue(this, "orderQueue", QueueProps.builder()
                .queueName("OrderQueue")
                .contentBasedDeduplication(true)
                .fifo(true)
                .build());
        orderQueueUrl = orderQueue.getQueueUrl();
    }
}
