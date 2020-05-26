package com.example.bloodbank.blockchain;

import com.example.bloodbank.model.Blood;
import com.example.bloodbank.model.OrderStatus;
import com.example.bloodbank.model.TransferType;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class Order extends Transaction {
    private OrderStatus orderStatus;
    private String hash;

    public Order(String to_id, String from_id, List<Blood> blood_list, TransferType transaction_type, OrderStatus orderStatus) {
        super(to_id, from_id, blood_list, transaction_type);
        this.orderStatus = orderStatus;
        this.hash=calculateHash();
    }


    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String calculateHash(){
        return Hashing.sha256()
                .hashString(this.getTo_id() +
                                this.getFrom_id() +
                                this.getBlood_list().toString()
                        , StandardCharsets.UTF_8)
                .toString();
    }
}
