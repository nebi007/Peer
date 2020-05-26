package com.example.bloodbank.controller;

import com.example.bloodbank.configs.ConnectionPool;
import com.example.bloodbank.configs.KeyManager;
import com.example.bloodbank.model.AccountDetails;
import com.example.bloodbank.model.NetworkConfig;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accountDetails")
public class AccountDetailsController {

    @Autowired
    AccountDetails accountDetail;

    @Autowired
    KeyManager keyManager;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ConnectionPool connectionPool;

    @PostMapping("/saveNetworkPorts")
    public String setNetworkConfigs(@RequestBody NetworkConfig networkConfig) {
        accountDetail.setName(networkConfig.getName());
        accountDetail.setNetworkPorts(networkConfig.getNetworkPorts());
        accountDetail.setPort(networkConfig.getPort());
        accountDetail.setPrivateKey(networkConfig.getPrivateKey());
        accountDetail.setPublicKey(networkConfig.getPublicKey());
        accountDetail.setAuthority(networkConfig.getAuthority());
        System.out.println(accountDetail);
        return "Raspuns";
    }

    @GetMapping("/getNameFromKey")
    public String getNameFromKey(@RequestHeader String publicKey){
        List<String> ports=connectionPool.getPorts();
        for(String port:ports){
            String name=restTemplate.postForObject("http://localhost:" + port + "/provideNameFromKey", publicKey, String.class);
            if(name!=null){
                return name;
            }
        }
        return null;
    }

    @RequestMapping("/provideNameFromKey")
    public String provideNameFromKey(@RequestBody String key){
        if(key.equals(keyManager.publicKeyEncoded)){
            return accountDetail.getName();
        }
        return null;
    }

    @GetMapping("/getKeyFromUsername")
    public String getKeyFromUserName(@RequestBody String username){
        List<String> ports=connectionPool.getPorts();
        for(String port:ports){
            String key=restTemplate.postForObject("http://localhost:" + port + "/provideKeyFromName",username, String.class);
            if(key!=null){
                return key;
            }
        }
        return null;
    }


    @GetMapping("/getKeyNames")
    public Map<String,String> recieveKeyName(){
        Map<String,String> map=new HashMap<>();
        List<String> ports=connectionPool.getPorts();
        for(String port:ports){
            String pair=restTemplate.postForObject("http://localhost:" + port + "/accountDetails/provideKeyName",null,String.class);
            String[] keyName=pair.split(":");
            if(keyName!=null){
                map.put(keyName[0],keyName[1]);
            }
        }
        return map;
    }

    @RequestMapping("/provideKeyName")
    public String provideKeyName(){
        String response=keyManager.publicKeyEncoded+":"+accountDetail.getName();
        return response;
    }

    @RequestMapping("/getBloodBankName")
    public String gerBloodBankName(@RequestBody String key){
        if(keyManager.publicKeyEncoded.equals(key)){
            return accountDetail.getName();
        }
        return null;
    }

}
