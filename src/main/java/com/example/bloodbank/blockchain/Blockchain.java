package com.example.bloodbank.blockchain;

import com.example.bloodbank.configs.KeyManager;
import com.example.bloodbank.model.*;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class Blockchain {

    public List<Block> chain= new ArrayList<>();
    public List<Transaction> pendingTransactions=new ArrayList<>();
    public List<Order> orders =new ArrayList<>();

    public Blockchain() throws InvalidKeySpecException, NoSuchAlgorithmException {
        //initial set-up
        this.chain.add(createGenesisBlock());

        //the rest
        List<String> publicKeys=new ArrayList<>();
        List<String> privateKeys=new ArrayList<>();
        //donor  0
        publicKeys.add("MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAE3OeLglwsx1HqNMGUA4lo5G/zVcdNnHV0+vdjupbeSvMn+K/9Hh8bMmb2eyNNPe4M");
        privateKeys.add("MHsCAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQEEYTBfAgEBBBiw0WEL2ksFb7JG9RaVD49Rvm9uDkC2ZuKgCgYIKoZIzj0DAQGhNAMyAATc54uCXCzHUeo0wZQDiWjkb/NVx02cdXT692O6lt5K8yf4r/0eHxsyZvZ7I0097gw=");
        //bloodbank  1
        publicKeys.add("MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEdU7l168yyuixp3FJKpLPIhb8SM380tTIeab7SHV6DEKrCNom/mZ9nWLAW1B7mC+N");
        privateKeys.add("MHsCAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQEEYTBfAgEBBBgesGzYyIW+Xf/1pMVne7+xUVQ+n9jhdu+gCgYIKoZIzj0DAQGhNAMyAAR1TuXXrzLK6LGncUkqks8iFvxIzfzS1Mh5pvtIdXoMQqsI2ib+Zn2dYsBbUHuYL40=");
        //hospital   2
        publicKeys.add("MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEI+73qsPJCH+J/PCzsJo+eiWTEjPaCpVIBG8ofjj+cKMuKnavKwSmja+ii9sIMf3D");
        privateKeys.add("MHsCAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQEEYTBfAgEBBBiY83pIlSqusFk51nKgepnXD0ramtiq1JWgCgYIKoZIzj0DAQGhNAMyAAQj7veqw8kIf4n88LOwmj56JZMSM9oKlUgEbyh+OP5woy4qdq8rBKaNr6KL2wgx/cM=");
        //receptionist   3
        publicKeys.add("MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAE7QVriy7vFOnfPBE0r1ujknLdp8VZmorHPd+dWPWkzi6ssEwMjlGdXMsoBeUKosSP");
        privateKeys.add("MHsCAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQEEYTBfAgEBBBjN9Z9mBG+F297cgXQ7DDIYJne8323prP2gCgYIKoZIzj0DAQGhNAMyAATtBWuLLu8U6d88ETSvW6OSct2nxVmaisc9351Y9aTOLqywTAyOUZ1cyygF5QqixI8=");





        List<Blood> bloodTransfered1=new ArrayList<>();
        bloodTransfered1.add(new Blood(BloodType.A2, RhType.Positive,200,publicKeys.get(0)));
        Transaction t1=new Transaction(publicKeys.get(1),publicKeys.get(3),bloodTransfered1, TransferType.Donation);
        t1.generateSignature(KeyManager.decodePrivateKey(privateKeys.get(3)));
        this.pendingTransactions.add(t1);
        Block block1=new Block(Timestamp.valueOf("2007-09-24 10:10:10.0"), Arrays.asList(t1),getLatestBlock().getHash());
        this.chain.add(block1);
        this.pendingTransactions.clear();

        List<Blood> bloodTransfered2=new ArrayList<>();
        bloodTransfered2.add(new Blood(BloodType.A2, RhType.Positive,200,publicKeys.get(0)));
        Transaction t2=new Transaction(publicKeys.get(2),publicKeys.get(1),bloodTransfered2,TransferType.Transfer);
        t2.generateSignature(KeyManager.decodePrivateKey(privateKeys.get(1)));

        List<Blood> bloodTransfered3=new ArrayList<>();
        bloodTransfered3.add(new Blood(BloodType.A2, RhType.Positive,200,publicKeys.get(0)));
        Transaction t3=new Transaction(publicKeys.get(1),publicKeys.get(3),bloodTransfered3,TransferType.Donation);
        t3.generateSignature(KeyManager.decodePrivateKey(privateKeys.get(3)));

        this.pendingTransactions.add(t2);
        this.pendingTransactions.add(t3);
        Block block2=new Block(Timestamp.valueOf("2007-09-25 10:10:10.0"), Arrays.asList(t2,t3),getLatestBlock().getHash());
        this.chain.add(block2);
        this.pendingTransactions.clear();

        List<Blood> orderBlood=bloodTransfered3;
        Order o1=new Order(publicKeys.get(1),publicKeys.get(2),orderBlood,TransferType.Order,OrderStatus.PENDING);
        this.createOrder(o1);
        Block block3=new Block(Timestamp.valueOf("2007-09-25 10:10:10.0"), Arrays.asList(o1),getLatestBlock().getHash());
        this.chain.add(block3);
        System.out.println(this.orders.get(0).getOrderStatus());
        this.pendingTransactions.clear();
        returnRandomKeyBasedOnWeights("MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAE4b9LaMkn0vsATDbNXw2xBgkHT+fDVB5MXcZ3q5rNS96spzgn1Y90OflGkCfL9Ih1");
    }

    private Block createGenesisBlock(){
        return new Block(Timestamp.valueOf("2007-09-23 10:10:10.0"),new ArrayList<>(),"Genesis Block");
    }

    public String returnRandomKeyBasedOnWeights(String excludedKey){
        Float total = 0f;
        Map<String,Float> weightedKeys=new HashMap<>();
        List<String> keyList=getAllPublicKeys();
        keyList.remove(excludedKey);

        for(String key:keyList){
            total=total+ getBalanceForBloodBank(key).size();
        }

        for(String key:keyList){
            weightedKeys.put(key, getBalanceForBloodBank(key).size()/total);
        }

        double random=Math.random();
        String randomKey="";


        for(String key:weightedKeys.keySet()){
            random-=weightedKeys.get(key);
            if(random <= 0.0d){
                randomKey=key;
                break;
            }
        }

        return randomKey;
    }



    public Block getLatestBlock(){
        return chain.get(chain.size()-1);
    }

    public void miningPendingTransactions(Integer id_miner){
        Block block=new Block(Timestamp.valueOf(LocalDateTime.now()),pendingTransactions);
    }

    public List<Blood> getBalanceForBloodBank(String id){
        List<Blood> bloodBalance=new ArrayList<>();
        chain.forEach(block->{
            block.getTransactions().forEach(transaction->{
                if(transaction.getTo_id().equals(id) && transaction.getTransaction_type().equals(TransferType.Donation)){
                    transaction.getBlood_list().forEach(
                            blood->{
                                bloodBalance.add(blood);
                            }
                    );
                }else{
                    if(transaction.getFrom_id().equals(id) && transaction.getTransaction_type().equals(TransferType.Transfer)){
                        transaction.getBlood_list().forEach(blood -> {
                            int i=0;
                            int foundIndex=-1;
                            for(Blood b:bloodBalance){
                                if(b.getQuantity()==blood.getQuantity() && b.getBlood_type().equals(blood.getBlood_type()) && b.getRh_type().equals((blood.getRh_type())) && b.getDonor_id().equals(blood.getDonor_id())){
                                    foundIndex=i;
                                }
                                i++;
                            }
                            if(foundIndex!=-1){
                                bloodBalance.remove(foundIndex);
                            }
//                            if(bloodBalance.contains(blood)){
//                                bloodBalance.remove(blood);
//                            }
                        });
                    }
                }
            });
        });
        return bloodBalance;
    }


    public List<Transaction> getDonationHistory(String id){
        List<Transaction> donations=new ArrayList<>();
        chain.forEach(block -> {
            block.getTransactions().forEach(transaction -> {
                if(transaction.getTransaction_type().equals(TransferType.Donation)){
                    transaction.getBlood_list().forEach(blood -> {
                        if(blood.getDonor_id().equals(id))
                            donations.add(transaction);
                    });
                }
            });
        });
        return donations;
    }


//    public List<Transaction> getOrdersForBloodbank(String id){
//        List<Transaction> orders=new ArrayList<>();
//        chain.forEach(block -> {
//            block.getTransactions().forEach(transaction -> {
//                if(transaction.getTransaction_type().equals(TransferType.Order) && transaction.getTo_id().equals(id)){
//                    orders.add(transaction);
//                }
//            });
//        });
//        return orders;
//    }


    public List<Transaction> getOrdersForHospital(String id){
        List<Transaction> orders=new ArrayList<>();
        chain.forEach(block -> {
            block.getTransactions().forEach(transaction -> {
                if(transaction.getTransaction_type().equals(TransferType.Order) && transaction.getFrom_id().equals(id)){
                    orders.add(transaction);
                }
            });
        });
        return orders;
    }

    public List<Transaction> getReceptionistHistory(String id){
        List<Transaction> donations=new ArrayList<>();
        chain.forEach(block -> {
            block.getTransactions().forEach(transaction -> {
                if(transaction.getFrom_id().equals(id) && transaction.getTransaction_type().equals(TransferType.Donation)){
                            donations.add(transaction);
                }
            });
        });
        return donations;
    }

    public Integer getLivesSaved(String id){
        List<Blood> bloodList=new ArrayList<>();
        chain.forEach(block -> {
            block.getTransactions().forEach(transaction -> {
                transaction.getBlood_list().forEach(blood->{
                    if(blood.getDonor_id().equals(id) && transaction.getTransaction_type().equals(TransferType.Transfer)){
                        bloodList.add(blood);
                    }
                });
            });
        });

        return bloodList.size();
    }


    private List<String> getAllPublicKeys(){
        List<String> keys=new ArrayList<>();
        chain.forEach(block -> {
            block.getTransactions().forEach(transaction -> {
                if(!keys.contains(transaction.getTo_id()) && transaction.getTransaction_type().equals(TransferType.Donation)){
                    keys.add(transaction.getTo_id());
                }
            });
        });
        return keys;
    }

    public void addTransaction(Transaction transaction) throws Exception{
        if(transaction.getFrom_id()==null || transaction.getTo_id()==null ){
            throw new Exception("From or To id are null");
        }
        if(!transaction.verifySignature()){
            throw new Exception("Signature is not valid");
        }
        if(transaction.getBlood_list().size()==0){
            throw new Exception("Blood list is empty");
        }
        this.pendingTransactions.add(transaction);
    }

    public Block minePendingTransactions() {
        List<Transaction> transactions=new ArrayList<>();
        this.pendingTransactions.forEach(t->{
            transactions.add(t);
        });
        Block block=new Block(Timestamp.valueOf(LocalDateTime.now()),transactions,this.getLatestBlock().getHash());
        if(block.hasValidTransactions())
            if(chain.add(block)) {
                return block;
            }
        return null;
    }


    public boolean addBlock(Block block){
        return this.chain.add(block);
    }


    public boolean isChainValid(){
        for(int i=1;i<chain.size();i++){
            Block currentBlock=this.chain.get(i);
            Block previousBlock=this.chain.get(i-1);
            if(!currentBlock.getHash().equals(currentBlock.calculateHash())){
                return false;
            }

            if(!currentBlock.getPreviousHash().equals(previousBlock.getHash())){
                return false;
            }
        }
        return true;
    }

    public void createOrder(Order order){
        this.orders.add(order);
    }


    public void cancelOrder(Order order){
        orders.forEach(pOrder->{
            if(pOrder.getHash().equals(order.getHash())){
                pOrder.setOrderStatus(OrderStatus.CANCELED);
            }
        });
    }


    public void completeOrder(Order order){
        orders.forEach(pOrder->{
            if(pOrder.getHash().equals(order.getHash())){
                pOrder.setOrderStatus(OrderStatus.COMPLETED);
            }
        });
    }

    public void print(){
        chain.forEach(x-> System.out.println(x));
    }


    public List<Order> getOrdersForBloodbank(String key){
        List<Order> orderList=new ArrayList<>();
        this.orders.forEach(order->{
            if(order.getTo_id().equals(key) && order.getOrderStatus().equals(OrderStatus.PENDING)){
                orderList.add(order);
            }
        });

        return orderList;
    }

    public List<Order> getOrdersByHospital(String key){
        List<Order> orderList=new ArrayList<>();
        this.orders.forEach(order->{
            if(order.getFrom_id().equals(key)){
                orderList.add(order);
            }
        });

        return orderList;
    }
}
