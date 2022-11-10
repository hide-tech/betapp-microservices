package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class PaymentServiceStack extends Stack {

    public String paymentQueueUrl;

    public PaymentServiceStack(final Construct scope, final String id) {
        this(scope, id, null, null, null);
    }

    public PaymentServiceStack(final Construct scope,
                               final String id,
                               final StackProps props,
                               final String orderQueueUrl,
                               final String secret) {
        super(scope, id, props);


    }
}
