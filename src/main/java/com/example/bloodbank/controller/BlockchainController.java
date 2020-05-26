package com.example.bloodbank.controller;

import com.example.bloodbank.blockchain.Block;
import com.example.bloodbank.blockchain.Blockchain;
import com.example.bloodbank.blockchain.Order;
import com.example.bloodbank.blockchain.Transaction;
import com.example.bloodbank.configs.ConnectionPool;
import com.example.bloodbank.configs.KeyManager;
import com.example.bloodbank.message.OrderInformation;
import com.example.bloodbank.message.Response;
import com.example.bloodbank.message.TransactionInformation;
import com.example.bloodbank.model.AccountDetails;
import com.example.bloodbank.model.Blood;
import com.example.bloodbank.model.OrderStatus;
import com.example.bloodbank.model.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/blockchain")
public class BlockchainController {

    @Autowired
    AccountDetails accountDetails;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Blockchain blockchain;

    @Autowired
    KeyManager keyManager;

    @Autowired
    ConnectionPool connectionPool;


    @RequestMapping("/initBlockChain")
    public void sendInitialBlockChain() {
        //porturile de pe active
        connectionPool.getPorts().forEach(port -> {
            String response = restTemplate.postForObject("http://localhost:" + port + "/blockchain/reciveInitialBlockChain", blockchain, String.class);
            System.out.println("Port " + port + "  " + response);
        });
    }

    @GetMapping("/noOfBloodbanksActive")
    public Integer getNoOfBloodBanksActive(){
        //porturi de la account detail
        List<String> activePorts=new ArrayList<>();
        connectionPool.getPorts().forEach(port->{

            try {
                Boolean active = restTemplate.postForObject("http://localhost:" + port + "/blockchain/isActive", null, Boolean.class);
                if(active==true)
                    activePorts.add(port);
            } catch (Exception e) {
                //Exception is thrown because the conection is closed
                System.out.println(port+" is closed or not a blood bank!");
//                activePorts.put(port, false);
            }
        });
        return activePorts.size();
    }

    @PostMapping(value = "/reciveInitialBlockChain")
    public String reciveInitialBlockChain(@RequestBody Blockchain initialBlockChain) {
        System.out.println("Am primit blocu");
        System.out.println(initialBlockChain.chain.toString());
        return "succes";
    }

    @PostMapping(value = "/sendToPendingTransactions")
    public Response sendToPendingTransactions(@RequestBody TransactionInformation transactionInfo) throws Exception {
        //notificari prin account detail
        try{
            List<Blood> bloodTransfered = new ArrayList<>();

        System.out.println(transactionInfo);
        bloodTransfered.add(transactionInfo.getBlood());
        System.out.println(keyManager.getPublicKey());

        Transaction t = new Transaction(transactionInfo.getTo_address(), keyManager.publicKeyEncoded, bloodTransfered, TransferType.Donation);
        t.generateSignature(keyManager.getPrivateKey());
        blockchain.addTransaction(t);

        connectionPool.getPorts().forEach(port -> {
            String response = restTemplate.postForObject("http://localhost:" + port + "/blockchain/reciveToPendingTransactions", t, String.class);
            System.out.println("Port " + port + "  " + response);
        });
        return new Response("The donation was executed","OK");
        }catch (Exception e ){
            e.printStackTrace();
            return new Response(e.getMessage(),"ERROR");
        }
    }


    public Response sendTransferToPendingTransactions(Transaction t) throws Exception {
        blockchain.addTransaction(t);
        connectionPool.getPorts().forEach(port -> {
            String response = restTemplate.postForObject("http://localhost:" + port + "/blockchain/reciveToPendingTransactions", t, String.class);
            System.out.println("Port " + port + "  " + response);
        });

        return new Response("OK","Transaction sent");
    }

    @PostMapping(value = "/reciveToPendingTransactions")
    public String reciveToPendingTransactions(@RequestBody Transaction transaction) throws Exception {
        blockchain.addTransaction(transaction);
        System.out.println(blockchain.pendingTransactions);
        System.out.println(transaction.verifySignature());
        return "succes";
    }

    @RequestMapping("/addBlock")
    public void addBlock(@RequestBody Block block) {
        System.out.println("Am adaugat un block");
        blockchain.addBlock(block);
        blockchain.pendingTransactions.clear();
    }

    @RequestMapping("/isActive")
    public boolean isActive() {
        if(this.accountDetails.getAuthority().equals("ROLE_BLOOD_BANK")){
            return true;
        }
        return false;
    }

    @GetMapping("/getBlockchain")
    public List<Block> getBlockchain() {
        return this.blockchain.chain;
    }


    @PostMapping("/mineBlock")
    public void mineBlock(@RequestBody String address) {
        //notificari prin netwokr ports de la account info
        if (keyManager.publicKeyEncoded.equals(address)) {
            System.out.println("I a m the miner");
            List<String> ports = connectionPool.getPorts();
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            String nextMiner = blockchain.returnRandomKeyBasedOnWeights(keyManager.publicKeyEncoded);


            executorService.schedule(() -> {
                Block block = blockchain.minePendingTransactions();
                this.blockchain.pendingTransactions.clear();
                ports.forEach(port -> {
                    restTemplate.postForObject("http://localhost:" + port + "/blockchain/addBlock", block, Boolean.class);
                });

                Map<String, Boolean> activePorts = new HashMap<>();

                ports.forEach(port -> {
                    try {
                        Boolean active = restTemplate.postForObject("http://localhost:" + port + "/blockchain/isActive", null, Boolean.class);
                        activePorts.put(port, active);
                    } catch (Exception e) {
                        //Exception is thrown because the conection is closed
                        activePorts.put(port, false);
                    }
                });
                if (nextMiner.length() == 0 || !activePorts.containsValue(true)) {
                    mineBlock(keyManager.publicKeyEncoded);
                } else {
                    ports.forEach(port -> {
                        if (activePorts.get(port))
                            restTemplate.postForObject("http://localhost:" + port + "/blockchain/mineBlock", nextMiner, Boolean.class);
                    });
                }
            }, 60, TimeUnit.SECONDS);

        }
    }

    @RequestMapping("/isChainValid")
    public Boolean isChainValid(){
        return blockchain.isChainValid();
    }

    @PostMapping("/bloodBankBalance")
    public List<Blood> getBloodBankBalance(@RequestBody String id){
        return blockchain.getBalanceForBloodBank(id);
    }

    @PostMapping("/bloodBankBalanceForHospital")
    public List<Blood> getBloodBankBalanceForHospital(@RequestBody String id){
        List<Blood> bloodForHospital=blockchain.getBalanceForBloodBank(id);
        this.blockchain.orders.forEach(order->{
            if(order.getFrom_id().equals(keyManager.publicKeyEncoded) && order.getTo_id().equals(id) && order.getOrderStatus().equals(OrderStatus.PENDING)){
                order.getBlood_list().forEach(blood->{
                    Blood newBlood=blood;
                    int i=0;
                    int findIndex=-1;
                    for(Blood b:bloodForHospital){
                        if(b.getDonor_id()==blood.getDonor_id() && b.getBlood_type()==blood.getBlood_type() && b.getRh_type()==blood.getRh_type() && b.getQuantity()==blood.getQuantity()){
                            findIndex=i;
                        }
                        i++;
                    }
                    bloodForHospital.remove(findIndex);
                });
            }
        });
        return bloodForHospital;
    }

    @GetMapping("/donationHistory")
    public List<Transaction> getDonationHistory(){
        System.out.println(accountDetails.getName());
        System.out.println(accountDetails.getNetworkPorts());
        System.out.println(accountDetails.getPort());

        return blockchain.getDonationHistory(accountDetails.getPublicKey());
    }

    @GetMapping("/bloodBankBalanceForBloodBank")
    public List<Blood> getBloodBankBalance(){
        return this.blockchain.getBalanceForBloodBank(keyManager.publicKeyEncoded);
    }

    @GetMapping("/receptionistHistory")
    public List<Transaction> getReceptionistHistory(){
        return blockchain.getReceptionistHistory("MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAE7QVriy7vFOnfPBE0r1ujknLdp8VZmorHPd+dWPWkzi6ssEwMjlGdXMsoBeUKosSP");
    }

    @GetMapping("/getLivesSaved")
    public Integer getLivesSaved(){
        return blockchain.getLivesSaved(accountDetails.getPublicKey());
    }


    @GetMapping("/getOrdersForBloodbank")
    public List<Order> getOrdersForBloodbank(){
        return this.blockchain.getOrdersForBloodbank(keyManager.publicKeyEncoded);
    }

    @GetMapping("/getOrdersByHospital")
    public List<Order> getOrdersByHospital(){
        return this.blockchain.getOrdersByHospital(keyManager.publicKeyEncoded);
    }

    @PostMapping("/cancelOrder")
    public Response cancelOrder(@RequestBody OrderInformation orderInfo){
        //from hospital
        //to bloodbank
        //keymanager
        Order order=new Order(keyManager.publicKeyEncoded,orderInfo.getTo_id(),orderInfo.getBloodList(),TransferType.Order, OrderStatus.PENDING);
        try{
            this.blockchain.cancelOrder(order);

            return new Response("The order was canceled","OK");
        }catch (Exception e){
            return new Response(e.getMessage(),"ERROR");
        }
    }


    @PostMapping("/createOrder")
    public Response createOrder(@RequestBody OrderInformation orderInfo){
        // trebe inlocuit keymanager cu account info
        //trebe notificata toata lumea de adaugarea unui order
        Order order=new Order(orderInfo.getTo_id(),keyManager.publicKeyEncoded,orderInfo.getBloodList(),TransferType.Order, OrderStatus.PENDING);
        try{
            this.blockchain.createOrder(order);
            this.connectionPool.getPorts().forEach(port->{try {
                restTemplate.postForObject("http://localhost:" + port + "/blockchain/addOrder", order, String.class);
            }catch (Exception e){
                System.out.println("Port is not active "+port);
            }
            });
            return new Response("The order was created","OK");
        }catch (Exception e){
            return new Response(e.getMessage(),"ERROR");
        }
    }

    @RequestMapping("/addOrder")
    public void addOrder(@RequestBody Order order){
        this.blockchain.orders.add(order);
    }


    @PostMapping("/completeOrder")
    public Response completeOrder(@RequestBody OrderInformation orderInfo){
        //trebuie notificata toata lumea de faptul ca s-a schimbat cererea
        //trebele inlocuit keymanager
        Order order=new Order(keyManager.publicKeyEncoded,orderInfo.getTo_id(),orderInfo.getBloodList(),TransferType.Order, OrderStatus.PENDING);
        try{
            this.blockchain.completeOrder(order);
            Transaction t=new Transaction(orderInfo.getTo_id(),keyManager.publicKeyEncoded,orderInfo.getBloodList(),TransferType.Transfer);
            t.generateSignature(keyManager.getPrivateKey());
            sendTransferToPendingTransactions(t);
            return new Response("The order was completed","OK");
        }catch (Exception e){
            return new Response(e.getMessage(),"ERROR");
        }
    }

}
