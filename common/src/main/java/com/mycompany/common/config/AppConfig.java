package com.mycompany.common.config;

public class AppConfig {
    private String kinesisStreamName = "default-stream";
    private String region = "us-east-1";
    private String environment = "dev";
    private String serviceName;
    private int batchSize = 100;

    public String getKinesisStreamName() {
        return kinesisStreamName;
    }

    public void setKinesisStreamName(String kinesisStreamName) {
        this.kinesisStreamName = kinesisStreamName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}