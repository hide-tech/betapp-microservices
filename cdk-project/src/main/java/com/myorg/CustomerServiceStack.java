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

public class CustomerServiceStack extends Stack {

    public final String customerPostgresUsername = "postgresUser";
    public final String customerPostgresPass = "postgresPass";
    public final String customerDatabaseName = "customers";
    public String customerQueueUrl;
    public CfnResource cfnResource;
    public ApplicationLoadBalancedFargateService loadBalancer;
    public String customerServiceDbUrl;

    public CustomerServiceStack(final Construct scope, final String id) {
        this(scope, id, null, null, null, null);
    }

    public CustomerServiceStack(final Construct scope,
                                final String id,
                                final StackProps props,
                                final String orderQueueUrl,
                                final String secretKey,
                                final String accessId) {
        super(scope, id, props);

        Queue queue = new Queue(this, "CustomerQueue", QueueProps.builder()
                .fifo(true)
                .contentBasedDeduplication(true)
                .queueName("CustomerQueue.fifo")
                .build());

        this.customerQueueUrl = queue.getQueueUrl();

        Vpc vpc = new Vpc(this, "customer-service-vpc", VpcProps.builder()
                .maxAzs(2)
                .natGateways(1)
                .build());

        DatabaseInstance customerDb = new DatabaseInstance(this, "customer-database",
                DatabaseInstanceProps.builder()
                        .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                                        .version(PostgresEngineVersion.VER_13_3)
                                .build()))
                        .instanceType(InstanceType.of(InstanceClass.T3, InstanceSize.MICRO))
                        .vpc(vpc)
                        .availabilityZone(vpc.getAvailabilityZones().get(0))
                        .allocatedStorage(200)
                        .multiAz(false)
                        .databaseName(customerDatabaseName)
                        .credentials(Credentials.fromPassword(customerPostgresUsername, SecretValue.unsafePlainText(customerPostgresPass)))
                        .build());

        String dbInstanceSocketAddress = customerDb.getInstanceEndpoint().getSocketAddress();
        String dbConnectUri = "jdbc:postgresql://" + dbInstanceSocketAddress + "/" + customerDatabaseName;
        customerServiceDbUrl = dbConnectUri;

        Cluster cluster = new Cluster(this, "customer-service-cluster", ClusterProps.builder()
                .clusterName("customerServiceCluster")
                .vpc(vpc)
                .build());

        Map<String, String> containerEnv = new HashMap<>();
        containerEnv.put("CLOUD_AWS_CREDENTIALS_ACCESS-KEY", accessId);
        containerEnv.put("CLOUD_AWS_CREDENTIALS_SECRET-KEY", secretKey);
        containerEnv.put("CLOUD_AWS_REGION_STATIC", props.getEnv().getRegion());

        ApplicationLoadBalancedFargateService serviceApp = new ApplicationLoadBalancedFargateService(
               this, "customer-service-load-balancer", ApplicationLoadBalancedFargateServiceProps.builder()
                .assignPublicIp(false)
                .cluster(cluster)
                .desiredCount(2)
                .cpu(256)
                .memoryLimitMiB(512)
                .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                        .image(ContainerImage.fromAsset("../back/customer-service/customer-service",
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
        cfnProps.put("Name", "V2 vpc link customer-service");
        cfnProps.put("SubnetIds", vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId).collect(Collectors.toList()));

        CfnResource httpVpcLink = new CfnResource(this, "HttpVpcLinkCustomer", CfnResourceProps.builder()
                .type("AWS::ApiGatewayV2::VpcLink")
                .properties(cfnProps)
                .build());
        cfnResource = httpVpcLink;
    }
}
