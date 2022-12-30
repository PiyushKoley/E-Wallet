package com.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;


    //****************  CREATEWALLET WILL BE CALLED THROUGH KAFKA **********
    // *********** when ever any user is created then the wallet gets created automatically *************
    @KafkaListener(topics = "create_wallet", groupId = "friends_group")
    void createWallet(String message) throws JsonProcessingException {

        JSONObject walletRequest = objectMapper.readValue(message,JSONObject.class);

        String username = (String) walletRequest.get("userName");

        WalletEntity walletEntity = WalletEntity.builder()
                                    .userName(username)
                                    .amount(0)
                                    .build();

        walletRepository.save(walletEntity);
    }


    @KafkaListener(topics = "update_wallet", groupId = "friends_group")
    void updateWallet(String message) throws JsonProcessingException {
        JSONObject walletRequest = objectMapper.readValue(message,JSONObject.class);

        String fromUser = (String) walletRequest.get("fromUser");
        String toUser =(String) walletRequest.get("toUser");
        int transactionAmount = (int) walletRequest.get("amount");
        String transactionId = (String) walletRequest.get("transactionId");


        // TODO STEPS :
        /*
            1st check sender balance .
                -> if balanceOfSender < transactionAmount => send status as failed.

            2nd

        * */

        WalletEntity senderWallet = walletRepository.findByUserName(fromUser);
        WalletEntity receiverWallet = walletRepository.findByUserName(toUser);

        JSONObject transactionRequest = new JSONObject();
        transactionRequest.put("transactionId",transactionId);

        if(senderWallet.getAmount() < transactionAmount) { //****** insufficient fund in senders wallet *****
            transactionRequest.put("transactionStatus","FAILED");
        }
        else{ //****** if balance is sufficient then make transaction update wallet also...******
            transactionRequest.put("transactionStatus","SUCCESS");

            updateWallet(senderWallet,(-1*transactionAmount));
            updateWallet(receiverWallet,transactionAmount);

        }

        String sendMessage = transactionRequest.toJSONString();

        kafkaTemplate.send("update_transaction",sendMessage);
    }

    private void updateWallet(WalletEntity walletEntity, int amount) {
        int newAmount = walletEntity.getAmount() + amount;

        walletEntity.setAmount(newAmount);

        walletRepository.save(walletEntity);
    }



}
