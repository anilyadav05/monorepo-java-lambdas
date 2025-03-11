package com.mycompany.infra;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class CdkApp {
    public static void main(final String[] args) {
        App app = new App();

        String environment = (String) app.getNode().tryGetContext("environment");
        if (environment == null) {
            environment = "dev";
        }

        Environment awsEnv = Environment.builder()
                .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                .region(System.getenv("CDK_DEFAULT_REGION"))
                .build();

        StackProps stackProps = StackProps.builder()
                .env(awsEnv)
                .build();

        // Create infrastructure stacks
        new KinesisStack(app, "KinesisStack-" + environment, stackProps, environment);
        new SqsStack(app, "SqsStack-" + environment, stackProps, environment);
        new LambdaStack(app, "LambdaStack-" + environment, stackProps, environment);
        new EventBridgeStack(app, "EventBridgeStack-" + environment, stackProps, environment);

        app.synth();
    }
}