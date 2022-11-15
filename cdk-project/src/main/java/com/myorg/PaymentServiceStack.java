package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.dynamodb.*;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateServiceProps;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.sqs.QueueProps;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

public class PaymentServiceStack extends Stack {

    public String paymentQueueUrl;
    public CfnResource cfnResource;
    public ApplicationLoadBalancedFargateService loadBalancer;

    public PaymentServiceStack(final Construct scope, final String id) {
        this(scope, id, null, null, null);
    }

    public PaymentServiceStack(final Construct scope,
                               final String id,
                               final StackProps props,
                               final String orderQueueUrl,
                               final String secret) {
        super(scope, id, props);

        Queue queue = new Queue(this, "PaymentQueue", QueueProps.builder()
                .fifo(true)
                .contentBasedDeduplication(true)
                .queueName("PaymentQueue.fifo")
                .build());

        paymentQueueUrl = queue.getQueueUrl();

        Vpc vpc = new Vpc(this, "payment-service-vpc", VpcProps.builder()
                .maxAzs(1)
                .natGateways(1)
                .build());

        Cluster cluster = new Cluster(this, "payment-service-cluster", ClusterProps.builder()
                .clusterName("paymentServiceCluster")
                .vpc(vpc)
                .build());

        Table dynamoTable = new Table(this, "payment-service-db", TableProps.builder()
                .tableName("invoices")
                .partitionKey(Attribute.builder()
                        .type(AttributeType.STRING)
                        .name("id")
                        .build())
                .billingMode(BillingMode.PROVISIONED)
                .build());

        Map<String, String> containerEnv = new HashMap<>();
        containerEnv.put("CLOUD_AWS_ACCESS-KEY", props.getEnv().getAccount());
        containerEnv.put("CLOUD_AWS_SECRET-KEY", secret);
        containerEnv.put("CLOUD_AWS_REGION", props.getEnv().getRegion());
        containerEnv.put("CLOUD_AWS_DYNAMO_ENDPOINT", dynamoTable.getTableName());
        containerEnv.put("CLOUD_AWS_QUEUE_RECEIVE-ENDPOINT", paymentQueueUrl);
        containerEnv.put("CLOUD_AWS_QUEUE_SEND-ENDPOINT", orderQueueUrl);

        ApplicationLoadBalancedFargateService serviceApp = new ApplicationLoadBalancedFargateService(
                this, "payment-service-load-balancer", ApplicationLoadBalancedFargateServiceProps.builder()
                .cluster(cluster)
                .desiredCount(2)
                .cpu(256)
                .memoryLimitMiB(512)
                .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                        .image(ContainerImage.fromAsset("../back/payment-service/payment-service",
                                AssetImageProps.builder()
//                                        .buildArgs(containerEnv)
                                        .build()))
                        .containerPort(8080)
                        .build())
                .build()
        );

        loadBalancer = serviceApp;

        dynamoTable.grantReadWriteData(serviceApp.getTaskDefinition().getTaskRole());

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
        cfnProps.put("Name", "V2 vpc link payment-service");
        cfnProps.put("SubnetIds", vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId));

        CfnResource httpVpcLink = new CfnResource(this, "HttpVpcLinkPayment", CfnResourceProps.builder()
                .type("AWS::ApiGatewayV2::VpcLink")
                .properties(cfnProps)
                .build());
        cfnResource = httpVpcLink;
    }
}
