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
import java.util.stream.Collectors;

public class ScheduleServiceStack extends Stack {

    public final String schedulePostgresUsername = "postgresUser";
    public final String schedulePostgresPass = "postgresPass";
    public final String scheduleDatabaseName = "schedules";
    public String orderQueueUrl;
    public CfnResource cfnResource;
    public ApplicationLoadBalancedFargateService loadBalancer;
    public String scheduleServiceDbUrl;

    public ScheduleServiceStack(final Construct scope, final String id) {
        this(scope, id, null, null);
    }

    public ScheduleServiceStack(final Construct scope,
                                final String id,
                                final StackProps props,
                                final String secretKey) {
        super(scope, id, props);

        Vpc vpc = new Vpc(this, "schedule-service-vpc", VpcProps.builder()
                .maxAzs(2)
                .natGateways(1)
                .build());

        DatabaseInstance scheduleDB = new DatabaseInstance(this, "schedule-database",
                DatabaseInstanceProps.builder()
                        .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_13_3)
                                .build()))
                        .instanceType(InstanceType.of(InstanceClass.T3, InstanceSize.MICRO))
                        .vpc(vpc)
                        .availabilityZone(vpc.getAvailabilityZones().get(0))
                        .allocatedStorage(200)
                        .multiAz(false)
                        .databaseName(scheduleDatabaseName)
                        .credentials(Credentials.fromPassword(schedulePostgresUsername, SecretValue.unsafePlainText(schedulePostgresPass)))
                        .build());

        String dbInstanceSocketAddress = scheduleDB.getInstanceEndpoint().getSocketAddress();
        String dbConnectUri = "jdbc:postgresql://" + dbInstanceSocketAddress + "/" + scheduleDatabaseName;
        scheduleServiceDbUrl = dbConnectUri;

        Cluster cluster = new Cluster(this, "schedule-service-cluster", ClusterProps.builder()
                .clusterName("scheduleServiceCluster")
                .vpc(vpc)
                .build());

        ApplicationLoadBalancedFargateService serviceApp = new ApplicationLoadBalancedFargateService(
                this, "schedule-service-load-balancer", ApplicationLoadBalancedFargateServiceProps.builder()
                .cluster(cluster)
                .desiredCount(2)
                .cpu(256)
                .memoryLimitMiB(512)
                .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                        .image(ContainerImage.fromAsset("../back/schedule-service/schedule-service",
                                AssetImageProps.builder()
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
        cfnProps.put("SubnetIds", vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId).collect(Collectors.toList()));

        CfnResource httpVpcLink = new CfnResource(this, "HttpVpcLinkSchedule", CfnResourceProps.builder()
                .type("AWS::ApiGatewayV2::VpcLink")
                .properties(cfnProps)
                .build());
        cfnResource = httpVpcLink;

        Queue orderQueue = new Queue(this, "orderQueue", QueueProps.builder()
                .queueName("OrderQueue.fifo")
                .contentBasedDeduplication(true)
                .fifo(true)
                .build());
        orderQueueUrl = orderQueue.getQueueUrl();
    }
}
