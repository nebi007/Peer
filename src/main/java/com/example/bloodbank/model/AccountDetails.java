package com.example.bloodbank.model;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountDetails {
    private String port;
    private List<String> networkPorts;
    private String name="cacapisu";
    private String privateKey;
    private String publicKey;
    private String authority;

    public AccountDetails() {
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<String> getNetworkPorts() {
        return networkPorts;
    }

    public void setNetworkPorts(List<String> networkPorts) {
        this.networkPorts = networkPorts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return "AccountDetails{" +
                "port='" + port + '\'' +
                ", networkPorts=" + networkPorts +
                ", name='" + name + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }
}
