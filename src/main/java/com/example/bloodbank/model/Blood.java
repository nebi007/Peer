package com.example.bloodbank.model;

import java.util.Objects;

public class Blood {
    private BloodType blood_type;
    private RhType rh_type;
    private int quantity;
    private String donor_id;

    public Blood(BloodType blood_type, RhType rh_type, int quantity, String donor_id) {
        this.blood_type = blood_type;
        this.rh_type = rh_type;
        this.quantity = quantity;
        this.donor_id = donor_id;
    }

    public BloodType getBlood_type() {
        return blood_type;
    }

    public void setBlood_type(BloodType blood_type) {
        this.blood_type = blood_type;
    }

    public RhType getRh_type() {
        return rh_type;
    }

    public void setRh_type(RhType rh_type) {
        this.rh_type = rh_type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDonor_id() {
        return donor_id;
    }

    public void setDonor_id(String donor_id) {
        this.donor_id = donor_id;
    }


    @Override
    public String toString() {
        return "Blood{" +
                "blood_type=" + blood_type +
                ", rh_type=" + rh_type +
                ", quantity=" + quantity +
                ", donor_id='" + donor_id + '\'' +
                '}';
    }
}
