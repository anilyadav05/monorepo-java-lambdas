package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

public class SqsConsumer implements RequestHandler<SQSEvent, String> {
    @Override
    public String handleRequest(SQSEvent event, Context context) {
        event.getRecords().forEach(record -> {
            context.getLogger().log("Received message: " + record.getBody());
        });
        return "Processed " + event.getRecords().size() + " messages.";
    }
}
