package com.example.bloodbank.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "connection-pool")
public class ConnectionPool
{
    List<String> ports = new ArrayList<>();

    public List<String> getPorts() {
        return ports;
    }

}
