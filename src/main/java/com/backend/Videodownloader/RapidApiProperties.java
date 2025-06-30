package com.backend.Videodownloader;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rapidapi")
public record RapidApiProperties(String key, String host) {}
