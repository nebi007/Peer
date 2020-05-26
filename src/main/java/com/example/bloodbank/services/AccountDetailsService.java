package com.example.bloodbank.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountDetailsService {

    private List<String> ports;
    private String myPort;

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }
}
