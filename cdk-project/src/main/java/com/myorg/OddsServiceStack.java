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
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.sqs.QueueProps;
import software.constructs.Construct;

import java.util.Map;

public class OddsServiceStack extends Stack {

    private final String oddsPostgresUsername = "postgresUser";
    private final String oddsPostgresPass = "postgresPass";
    private final String oddsDatabaseName = "odds";
    public String oddsQueueUrl;

    public OddsServiceStack(final Construct scope, final String id) {
        this(scope, id, null, null, null);
    }

    public OddsServiceStack(final Construct scope,
                            final String id,
                            final StackProps props,
                            final String orderQueueUrl,
                            final String secret) {
        super(scope, id, props);

        Queue queue = new Queue(this, "OddsQueue", QueueProps.builder()
                .fifo(true)
                .contentBasedDeduplication(true)
                .queueName("OddsQueue")
                .build());

        this.oddsQueueUrl = queue.getQueueUrl();

        Vpc vpc = new Vpc(this, "odds-service-vpc", VpcProps.builder()
                .maxAzs(1)
                .natGateways(1)
                .build());

        DatabaseInstance oddsDB = new DatabaseInstance(this, "odds-database",
                DatabaseInstanceProps.builder()
                        .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_13_7)
                                .build()))
                        .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                        .vpc(vpc)
                        .allocatedStorage(200)
                        .databaseName(oddsDatabaseName)
                        .credentials(Credentials.fromPassword(oddsPostgresUsername, SecretValue.plainText(oddsPostgresPass)))
                        .build());

        String dbInstanceSocketAddress = oddsDB.getInstanceEndpoint().getSocketAddress();
        String dbConnectUri = "jdbc:postgresql://" + dbInstanceSocketAddress + "/" + oddsDatabaseName;

        Cluster cluster = new Cluster(this, "odds-service-cluster", ClusterProps.builder()
                .clusterName("oddsServiceCluster")
                .vpc(vpc)
                .build());

        Map<String, String> containerEnv = Map.of("SPRING_DATASOURCE_URL",dbConnectUri,
                "SPRING_DATASOURCE_USERNAME", oddsPostgresUsername,
                "SPRING_DATASOURCE_PASSWORD", oddsPostgresPass,
                "SPRING_DATASOURCE_FLYWAY_URL", dbConnectUri,
                "SPRING_DATASOURCE_FLYWAY_USER", oddsPostgresUsername,
                "SPRING_DATASOURCE_FLYWAY_PASSWORD", oddsPostgresPass,
                "CLOUD_AWS_CREDENTIALS_ACCESS-KEY", props.getEnv().getAccount(),
                "CLOUD_AWS_CREDENTIALS_SECRET-KEY", secret,
                "CLOUD_AWS_REGION_STATIC", props.getEnv().getRegion(),
                "CLOUD_AWS_END-POINT_URL-RECEIVE", oddsQueueUrl);
        containerEnv.put("CLOUD_AWS_END-POINT_URL-SEND", orderQueueUrl);

        ApplicationLoadBalancedFargateService serviceApp = new ApplicationLoadBalancedFargateService(
                this, "odds-service-load-balancer", ApplicationLoadBalancedFargateServiceProps.builder()
                .cluster(cluster)
                .desiredCount(2)
                .cpu(256)
                .memoryLimitMiB(512)
                .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                        .image(ContainerImage.fromAsset("../back/odds-service/odds-service",
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
