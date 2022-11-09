package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class PaymentServiceStack extends Stack {

    public PaymentServiceStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public PaymentServiceStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


    }
}
