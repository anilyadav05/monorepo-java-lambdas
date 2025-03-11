package com.mycompany.infra;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

public class LambdaStack extends Stack {
    private final Function orderProcessorFunction;
    private final Function customerUpdaterFunction;
    private final Function inventoryCheckerFunction;

    public LambdaStack(final Construct scope, final String id, final StackProps props, final String environment) {
        super(scope, id, props);

        // IAM policy for Kinesis access
        PolicyStatement kinesisPolicy = PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .actions(List.of("kinesis:PutRecord", "kinesis:PutRecords"))
                .resources(List.of("*"))
                .build();

        // Order processor Lambda
        this.orderProcessorFunction = Function.Builder.create(this, "OrderProcessorFunction-" + environment)
                .functionName("order-processor-" + environment)
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../lambdas/order-processor/build/libs/order-processor.jar"))
                .handler("com.mycompany.lambdas.orderprocessor.OrderProcessorHandler")
                .memorySize(512)
                .timeout(Duration.seconds(60))
                .environment(Map.of(
                        "ENVIRONMENT", environment,
                        "LOG_LEVEL", "INFO"
                ))
                .build();

        orderProcessorFunction.addToRolePolicy(kinesisPolicy);

        // Customer updater Lambda
        this.customerUpdaterFunction = Function.Builder.create(this, "CustomerUpdaterFunction-" + environment)
                .functionName("customer-updater-" + environment)
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../lambdas/customer-updater/build/libs/customer-updater.jar"))
                .handler("com.mycompany.lambdas.customerupdater.CustomerUpdaterHandler")
                .memorySize(512)
                .timeout(Duration.seconds(60))
                .environment(Map.of(
                        "ENVIRONMENT", environment,
                        "LOG_LEVEL", "INFO"
                ))
                .build();

        customerUpdaterFunction.addToRolePolicy(kinesisPolicy);

        // Inventory checker Lambda
        this.inventoryCheckerFunction = Function.Builder.create(this, "InventoryCheckerFunction-" + environment)
                .functionName("inventory-checker-" + environment)
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../lambdas/inventory-checker/build/libs/inventory-checker.jar"))
                .handler("com.mycompany.lambdas.inventorychecker.InventoryCheckerHandler")
                .memorySize(512)
                .timeout(Duration.seconds(60))
                .environment(Map.of(
                        "ENVIRONMENT", environment,
                        "LOG_LEVEL", "INFO"
                ))
                .build();

        inventoryCheckerFunction.addToRolePolicy(kinesisPolicy);
    }

    public Function getOrderProcessorFunction() {
        return orderProcessorFunction;
    }

    public Function getCustomerUpdaterFunction() {
        return customerUpdaterFunction;
    }

    public Function getInventoryCheckerFunction() {
        return inventoryCheckerFunction;
    }
}