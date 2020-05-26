package com.example.bloodbank.blockchain;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

public class Block {
    public Block() {
    }

    private Timestamp timestamp;
    private List<Transaction> transactions;
    private String previousHash;
    private String hash;

    public Block(Timestamp timestamp, List<Transaction> transactions, String previousHash) {
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.hash=calculateHash();
    }

    public Block(Timestamp timestamp, List<Transaction> transactions) {
        this.timestamp = timestamp;
        this.transactions = transactions;
    }

    public boolean hasValidTransactions(){
        for(Transaction t : transactions){
            if(!t.verifySignature()){
                return false;
            }
        }
        return true;
    }


    public String calculateHash(){
        return Hashing.sha256()
                .hashString(this.previousHash +
                                this.timestamp.toString() +
                                this.transactions.toString()
                        , StandardCharsets.UTF_8)
                .toString();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Block{" +
                "timestamp=" + timestamp +
                ", transactions='" + transactions + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
