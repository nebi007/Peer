package com.example.bloodbank.blockchain;

import com.example.bloodbank.configs.KeyManager;
import com.example.bloodbank.model.Blood;
import com.example.bloodbank.model.TransferType;
import com.example.bloodbank.utils.StringUtil;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Transaction {
    private String to_id;
    private String from_id;
    private List<Blood> blood_list;
    private Timestamp timestamp;
    private TransferType transaction_type;
    public byte[] signature;
    public Transaction() {
    }

    public Transaction(String to_id, String from_id, List<Blood> blood_list, TransferType transaction_type) {
        this.to_id = to_id;
        this.from_id = from_id;
        this.blood_list = blood_list;
        this.timestamp = Timestamp.valueOf(LocalDateTime.now());
        this.transaction_type = transaction_type;
    }

    public void generateSignature(PrivateKey privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String data = StringUtil.getStringFromKey(KeyManager.decodePubliKey(from_id)) + StringUtil.getStringFromKey(KeyManager.decodePubliKey(to_id));
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = null;
        try {
            data = StringUtil.getStringFromKey(KeyManager.decodePubliKey(from_id)) + StringUtil.getStringFromKey(KeyManager.decodePubliKey(to_id));
            return StringUtil.verifyECDSASig(KeyManager.decodePubliKey(from_id), data, signature);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getTo_id() {
        return to_id;
    }

    public String getFrom_id() {
        return from_id;
    }

    public TransferType getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(TransferType transaction_type) {
        this.transaction_type = transaction_type;
    }

    public List<Blood> getBlood_list() {
        return blood_list;
    }

    public void setBlood_list(List<Blood> blood_list) {
        this.blood_list = blood_list;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "to_id='" + to_id + '\'' +
                ", from_id='" + from_id + '\'' +
                ", blood_list=" + blood_list +
                ", timestamp=" + timestamp +
                ", transaction_type='" + transaction_type + '\'' +
                ", signature=" + Arrays.toString(signature) +
                '}';
    }
}
