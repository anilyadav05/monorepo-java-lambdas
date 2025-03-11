package com.mycompany.infra;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.kinesis.Stream;
import software.amazon.awscdk.services.kinesis.StreamEncryption;
import software.constructs.Construct;

public class KinesisStack extends Stack {
    private final Stream orderEventsStream;
    private final Stream customerEventsStream;
    private final Stream inventoryEventsStream;

    public KinesisStack(final Construct scope, final String id, final StackProps props, final String environment) {
        super(scope, id, props);

        // Order events stream
        this.orderEventsStream = Stream.Builder.create(this, "OrderEventsStream-" + environment)
                .streamName("order-events-stream-" + environment)
                .shardCount(1)
                .retentionPeriod(Duration.hours(24))
                .encryption(StreamEncryption.MANAGED)
                .build();

        // Customer events stream
        this.customerEventsStream = Stream.Builder.create(this, "CustomerEventsStream-" + environment)
                .streamName("customer-events-stream-" + environment)
                .shardCount(1)
                .retentionPeriod(Duration.hours(24))
                .encryption(StreamEncryption.MANAGED)
                .build();

        // Inventory events stream
        this.inventoryEventsStream = Stream.Builder.create(this, "InventoryEventsStream-" + environment)
                .streamName("inventory-events-stream-" + environment)
                .shardCount(1)
                .retentionPeriod(Duration.hours(24))
                .encryption(StreamEncryption.MANAGED)
                .build();
    }

    public Stream getOrderEventsStream() {
        return orderEventsStream;
    }

    public Stream getCustomerEventsStream() {
        return customerEventsStream;
    }

    public Stream getInventoryEventsStream() {
        return inventoryEventsStream;
    }
}