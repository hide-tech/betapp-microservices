package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class OrderServiceStack extends Stack {

    public OrderServiceStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public OrderServiceStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


    }
}
