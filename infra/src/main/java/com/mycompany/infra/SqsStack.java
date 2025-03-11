package com.mycompany.infra;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

public class SqsStack extends Stack {
    private final Queue orderQueue;
    private final Queue customerQueue;
    private final Queue inventoryQueue;

    public SqsStack(final Construct scope, final String id, final StackProps props, final String environment) {
        super(scope, id, props);

        // Order queue with DLQ
        Queue orderDlq = Queue.Builder.create(this, "OrderDLQ-" + environment)
                .queueName("order-dlq-" + environment)
                .retentionPeriod(Duration.days(14))
                .build();

        DeadLetterQueue orderDeadLetterQueue = DeadLetterQueue.builder()
                .queue(orderDlq)
                .maxReceiveCount(3)
                .build();

        this.orderQueue = Queue.Builder.create(this, "OrderQueue-" + environment)
                .queueName("order-queue-" + environment)
                .visibilityTimeout(Duration.seconds(60))
                .retentionPeriod(Duration.days(4))
                .deadLetterQueue(orderDeadLetterQueue)
                .build();

        // Customer queue with DLQ
        Queue customerDlq = Queue.Builder.create(this, "CustomerDLQ-" + environment)
                .queueName("customer-dlq-" + environment)
                .retentionPeriod(Duration.days(14))
                .build();

        DeadLetterQueue customerDeadLetterQueue = DeadLetterQueue.builder()
                .queue(customerDlq)
                .maxReceiveCount(3)
                .build();

        this.customerQueue = Queue.Builder.create(this, "CustomerQueue-" + environment)
                .queueName("customer-queue-" + environment)
                .visibilityTimeout(Duration.seconds(60))
                .retentionPeriod(Duration.days(4))
                .deadLetterQueue(customerDeadLetterQueue)
                .build();

        // Inventory queue with DLQ
        Queue inventoryDlq = Queue.Builder.create(this, "InventoryDLQ-" + environment)
                .queueName("inventory-dlq-" + environment)
                .retentionPeriod(Duration.days(14))
                .build();

        DeadLetterQueue inventoryDeadLetterQueue = DeadLetterQueue.builder()
                .queue(inventoryDlq)
                .maxReceiveCount(3)
                .build();

        this.inventoryQueue = Queue.Builder.create(this, "InventoryQueue-" + environment)
                .queueName("inventory-queue-" + environment)
                .visibilityTimeout(Duration.seconds(60))
                .retentionPeriod(Duration.days(4))
                .deadLetterQueue(inventoryDeadLetterQueue)
                .build();
    }

    public Queue getOrderQueue() {
        return orderQueue;
    }

    public Queue getCustomerQueue() {
        return customerQueue;
    }

    public Queue getInventoryQueue() {
        return inventoryQueue;
    }
}