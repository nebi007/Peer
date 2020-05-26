package com.example.bloodbank.model;

import java.util.List;

public class NetworkConfig {
    private String port;
    private List<String> networkPorts;
    private String name;
    private String publicKey;
    private String privateKey;
    private String authority;

    public NetworkConfig(String port, List<String> networkPorts, String name,String publicKey,String privateKey,String authority) {
        this.port = port;
        this.networkPorts = networkPorts;
        this.name = name;
        this.publicKey=publicKey;
        this.privateKey=privateKey;
        this.authority=authority;
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
