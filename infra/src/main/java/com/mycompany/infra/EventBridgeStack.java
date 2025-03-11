package com.mycompany.infra;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.pipes.CfnPipe;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

public class EventBridgeStack extends Stack {

    public EventBridgeStack(final Construct scope, final String id, final StackProps props, final String environment) {
        super(scope, id, props);

        // Get references to resources from other stacks
        SqsStack sqsStack = (SqsStack) scope.getNode().findChild("SqsStack-" + environment);
        LambdaStack lambdaStack = (LambdaStack) scope.getNode().findChild("LambdaStack-" + environment);

        // Order Processing Pipe
        createEventBridgePipe(
                "OrderProcessingPipe-" + environment,
                "order-processing-pipe-" + environment,
                sqsStack.getOrderQueue(),
                lambdaStack.getOrderProcessorFunction(),
                environment
        );

        // Customer Updating Pipe
        createEventBridgePipe(
                "CustomerUpdatingPipe-" + environment,
                "customer-updating-pipe-" + environment,
                sqsStack.getCustomerQueue(),
                lambdaStack.getCustomerUpdaterFunction(),
                environment
        );

        // Inventory Checking Pipe
        createEventBridgePipe(
                "InventoryCheckingPipe-" + environment,
                "inventory-checking-pipe-" + environment,
                sqsStack.getInventoryQueue(),
                lambdaStack.getInventoryCheckerFunction(),
                environment
        );
    }

    private void createEventBridgePipe(String id, String name, Queue sourceQueue, Function targetFunction, String environment) {
        // Source policy document
        String sourcePolicy = String.format(
                "{\n" +
                        "  \"Version\": \"2012-10-17\",\n" +
                        "  \"Statement\": [\n" +
                        "    {\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": {\n" +
                        "        \"Service\": \"pipes.amazonaws.com\"\n" +
                        "      },\n" +
                        "      \"Action\": [\n" +
                        "        \"sqs:ReceiveMessage\",\n" +
                        "        \"sqs:DeleteMessage\",\n" +
                        "        \"sqs:GetQueueAttributes\"\n" +
                        "      ],\n" +
                        "      \"Resource\": \"%s\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}", sourceQueue.getQueueArn());

        // Target policy document
        String targetPolicy = String.format(
                "{\n" +
                        "  \"Version\": \"2012-10-17\",\n" +
                        "  \"Statement\": [\n" +
                        "    {\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": {\n" +
                        "        \"Service\": \"pipes.amazonaws.com\"\n" +
                        "      },\n" +
                        "      \"Action\": \"lambda:InvokeFunction\",\n" +
                        "      \"Resource\": \"%s\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}", targetFunction.getFunctionArn());

        // Create the pipe
        CfnPipe.Builder.create(this, id)
                .name(name)
                .roleArn("arn:aws:iam::" + getAccount() + ":role/service-role/Amazon_EventBridge_Pipe_" + name)
                .source(sourceQueue.getQueueArn())
                .sourceParameters(CfnPipe.PipeSourceParametersProperty.builder()
                        .sqsQueueParameters(CfnPipe.PipeSourceSqsQueueParametersProperty.builder()
                                .batchSize(5)
                                .maximumBatchingWindowInSeconds(60)
                                .build())
                        .build())
                .target(targetFunction.getFunctionArn())
                .targetParameters(CfnPipe.PipeTargetParametersProperty.builder()
                        .lambdaFunctionParameters(CfnPipe.PipeTargetLambdaFunctionParametersProperty.builder()
                                .invocationType("FIRE_AND_FORGET")  // or "REQUEST_RESPONSE"
                                .build())
                        .build())
                .build();

        // Add source and target policies
        sourceQueue.addToResourcePolicy(PolicyStatement.fromJson(sourcePolicy));
        targetFunction.addPermission("AllowInvocationFromPipe-" + environment,
                software.amazon.awscdk.services.lambda.Permission.builder()
                        .principal(new software.amazon.awscdk.services.iam.ServicePrincipal("pipes.amazonaws.com"))
                        .action("lambda:InvokeFunction")
                        .build());
    }
}