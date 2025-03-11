package com.mycompany.common.models;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseEvent {
    private String id;
    private Instant timestamp;
    private EventMetadata metadata;

    public BaseEvent() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.metadata = new EventMetadata();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public EventMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(EventMetadata metadata) {
        this.metadata = metadata;
    }
}