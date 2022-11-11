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

public class ResultServiceStack extends Stack {

    private final String resultPostgresUsername = "postgresUser";
    private final String resultPostgresPass = "postgresPass";
    private final String resultDatabaseName = "results";
    public String resultQueueUrl;
    public CfnResource cfnResource;
    public ApplicationLoadBalancedFargateService loadBalancer;

    public ResultServiceStack(final Construct scope, final String id) {
        this(scope, id, null, null, null);
    }

    public ResultServiceStack(final Construct scope,
                              final String id,
                              final StackProps props,
                              final String orderQueueUrl,
                              final String secret) {
        super(scope, id, props);

        Queue queue = new Queue(this, "ResultQueue", QueueProps.builder()
                .fifo(true)
                .contentBasedDeduplication(true)
                .queueName("ResultQueue")
                .build());

        resultQueueUrl = queue.getQueueUrl();

        Vpc vpc = new Vpc(this, "result-service-vpc", VpcProps.builder()
                .maxAzs(1)
                .natGateways(1)
                .build());

        DatabaseInstance resultDb = new DatabaseInstance(this, "result-database",
                DatabaseInstanceProps.builder()
                        .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_13_7)
                                .build()))
                        .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                        .vpc(vpc)
                        .allocatedStorage(200)
                        .databaseName(resultDatabaseName)
                        .credentials(Credentials.fromPassword(resultPostgresUsername, SecretValue.plainText(resultPostgresPass)))
                        .build());

        String dbInstanceSocketAddress = resultDb.getInstanceEndpoint().getSocketAddress();
        String dbConnectUri = "jdbc:postgresql://" + dbInstanceSocketAddress + "/" + resultDatabaseName;

        Cluster cluster = new Cluster(this, "result-service-cluster", ClusterProps.builder()
                .clusterName("resultServiceCluster")
                .vpc(vpc)
                .build());

        Map<String, String> containerEnv = new HashMap<>();
        containerEnv.put("SPRING_DATASOURCE_URL",dbConnectUri);
        containerEnv.put("SPRING_DATASOURCE_USERNAME", resultPostgresUsername);
        containerEnv.put("SPRING_DATASOURCE_PASSWORD", resultPostgresPass);
        containerEnv.put("SPRING_DATASOURCE_FLYWAY_URL", dbConnectUri);
        containerEnv.put("SPRING_DATASOURCE_FLYWAY_USER", resultPostgresUsername);
        containerEnv.put("SPRING_DATASOURCE_FLYWAY_PASSWORD", resultPostgresPass);
        containerEnv.put("CLOUD_AWS_CREDENTIALS_ACCESS-KEY", props.getEnv().getAccount());
        containerEnv.put("CLOUD_AWS_CREDENTIALS_SECRET-KEY", secret);
        containerEnv.put("CLOUD_AWS_REGION_STATIC", props.getEnv().getRegion());
        containerEnv.put("CLOUD_AWS_END-POINT_URL-RECEIVE", resultQueueUrl);
        containerEnv.put("CLOUD_AWS_END-POINT_URL-SEND", orderQueueUrl);

        ApplicationLoadBalancedFargateService serviceApp = new ApplicationLoadBalancedFargateService(
                this, "result-service-load-balancer", ApplicationLoadBalancedFargateServiceProps.builder()
                .cluster(cluster)
                .desiredCount(2)
                .cpu(256)
                .memoryLimitMiB(512)
                .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                        .image(ContainerImage.fromAsset("../back/result-service/result-service",
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
        cfnProps.put("Name", "V2 vpc link result-service");
        cfnProps.put("SubnetIds", vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId));

        CfnResource httpVpcLink = new CfnResource(this, "HttpVpcLinkResult", CfnResourceProps.builder()
                .type("AWS::ApiGatewayV2::VpcLink")
                .properties(cfnProps)
                .build());
        cfnResource = httpVpcLink;
    }
}
