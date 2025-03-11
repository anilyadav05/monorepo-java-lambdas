package com.mycompany.lambdas.orderprocessor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.mycompany.common.config.AppConfig;
import com.mycompany.common.config.ConfigLoader;
import com.mycompany.common.utils.AwsUtils;
import com.mycompany.common.utils.JsonUtils;
import com.mycompany.lambdas.orderprocessor.model.Order;
import com.mycompany.lambdas.orderprocessor.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderProcessorHandler implements RequestHandler<SQSEvent, Void> {
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessorHandler.class);
    private final AppConfig config;
    private final OrderService orderService;

    public OrderProcessorHandler() {
        this.config = ConfigLoader.loadConfig();
        this.orderService = new OrderService();
    }

    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        logger.info("Processing {} SQS messages", sqsEvent.getRecords().size());

        for (SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
            try {
                logger.info("Processing message: {}", message.getMessageId());

                // Parse the order from SQS message
                Order order = JsonUtils.fromJson(message.getBody(), Order.class);

                // Process the order
                Order processedOrder = orderService.processOrder(order);

                // Send to Kinesis
                String orderJson = JsonUtils.toJson(processedOrder);
                AwsUtils.putRecordToKinesis(
                        config.getKinesisStreamName(),
                        processedOrder.getId(),
                        orderJson
                );

                logger.info("Successfully processed order: {}", order.getId());
            } catch (Exception e) {
                logger.error("Error processing message: {}", message.getMessageId(), e);
                // In a real app, you might want to handle this differently
                // e.g., send to a DLQ
            }
        }

        return null;
    }
}