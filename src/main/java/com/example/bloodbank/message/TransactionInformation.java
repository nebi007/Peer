package com.example.bloodbank.message;

import com.example.bloodbank.model.Blood;

public class TransactionInformation {
    private String to_address;
    private Blood blood;

    public TransactionInformation(String to_address, Blood blood) {
        this.to_address = to_address;
        this.blood = blood;
    }

    public String getTo_address() {
        return to_address;
    }

    public void setTo_address(String to_address) {
        this.to_address = to_address;
    }

    public Blood getBlood() {
        return blood;
    }

    public void setBlood(Blood blood) {
        this.blood = blood;
    }

    @Override
    public String toString() {
        return "TransactionInformation{" +
                "to_address='" + to_address + '\'' +
                ", blood=" + blood +
                '}';
    }
}
