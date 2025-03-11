package com.mycompany.common.utils;

import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.nio.charset.StandardCharsets;

public class AwsUtils {
    private static final KinesisClient kinesisClient = KinesisClient.builder().build();
    private static final SqsClient sqsClient = SqsClient.builder().build();

    public static void putRecordToKinesis(String streamName, String partitionKey, String data) {
        PutRecordRequest request = PutRecordRequest.builder()
                .streamName(streamName)
                .partitionKey(partitionKey)
                .data(SdkBytes.fromString(data, StandardCharsets.UTF_8))
                .build();

        kinesisClient.putRecord(request);
    }

    public static KinesisClient getKinesisClient() {
        return kinesisClient;
    }

    public static SqsClient getSqsClient() {
        return sqsClient;
    }
}