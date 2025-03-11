package com.mycompany.lambdas.customerupdater;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.mycompany.common.config.AppConfig;
import com.mycompany.common.config.ConfigLoader;
import com.mycompany.common.utils.AwsUtils;
import com.mycompany.common.utils.JsonUtils;
import com.mycompany.lambdas.customerupdater.model.Customer;
import com.mycompany.lambdas.customerupdater.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerUpdaterHandler implements RequestHandler<SQSEvent, Void> {
    private static final Logger logger = LoggerFactory.getLogger(CustomerUpdaterHandler.class);
    private final AppConfig config;
    private final CustomerService customerService;

    public CustomerUpdaterHandler() {
        this.config = ConfigLoader.loadConfig();
        this.customerService = new CustomerService();
    }

    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        logger.info("Processing {} SQS messages", sqsEvent.getRecords().size());

        for (SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
            try {
                logger.info("Processing message: {}", message.getMessageId());

                // Parse the customer from SQS message
                Customer customer = JsonUtils.fromJson(message.getBody(), Customer.class);

                // Process the customer
                Customer processedCustomer = customerService.processCustomer(customer);

                // Send to Kinesis
                String customerJson = JsonUtils.toJson(processedCustomer);
                AwsUtils.putRecordToKinesis(
                        config.getKinesisStreamName(),
                        processedCustomer.getId(),
                        customerJson
                );

                logger.info("Successfully processed customer: {}", customer.getId());
            } catch (Exception e) {
                logger.error("Error processing message: {}", message.getMessageId(), e);
                // In a real app, you might want to handle this differently
                // e.g., send to a DLQ
            }
        }

        return null;
    }
}