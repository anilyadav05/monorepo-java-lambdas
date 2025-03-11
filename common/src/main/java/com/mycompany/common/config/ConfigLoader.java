package com.mycompany.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static AppConfig loadConfig() {
        try (InputStream inputStream = ConfigLoader.class.getResourceAsStream("/metadata.json")) {
            if (inputStream == null) {
                logger.warn("No metadata.json found, using default configuration");
                return new AppConfig();
            }
            return objectMapper.readValue(inputStream, AppConfig.class);
        } catch (IOException e) {
            logger.error("Failed to load configuration", e);
            return new AppConfig();
        }
    }
}