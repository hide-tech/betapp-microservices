package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class OddsServiceStack extends Stack {

    public OddsServiceStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public OddsServiceStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


    }
}
