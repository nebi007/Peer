package com.example.bloodbank.message;

import com.example.bloodbank.model.Blood;

import java.util.List;

public class OrderInformation {
    private String to_id;
    private List<Blood> bloodList;

    public OrderInformation(String to_id, List<Blood> bloodList) {
        this.to_id = to_id;
        this.bloodList = bloodList;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }

    public List<Blood> getBloodList() {
        return bloodList;
    }

    public void setBloodList(List<Blood> bloodList) {
        this.bloodList = bloodList;
    }
}
