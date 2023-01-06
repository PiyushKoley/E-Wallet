package com.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;

    void createTransaction(TransactionRequestDto transactionRequestDto) {

        TransactionEntity transactionEntity = TransactionEntity.builder()
                                                .fromUser(transactionRequestDto.getFromUser())
                                                .toUser(transactionRequestDto.getToUser())
                                                .amount(transactionRequestDto.getAmount())
                                                .status(TransactionStatus.PENDING)
                                                .transactionId(UUID.randomUUID().toString())
                                                .transactionTime(new Date().toString())
                                                .build();


        transactionEntity = transactionRepository.save(transactionEntity);

        // send a message to kafka to update the wallet of both the user ....

        JSONObject walletRequest = new JSONObject();

        walletRequest.put("fromUser",transactionEntity.getFromUser());
        walletRequest.put("toUser",transactionEntity.getToUser());
        walletRequest.put("amount",transactionEntity.getAmount());
        walletRequest.put("transactionId",transactionEntity.getTransactionId());

        String message = walletRequest.toJSONString();

        kafkaTemplate.send("update_wallet",message);



    }

    @KafkaListener(topics = "update_transaction", groupId = "Transactions_consumer_group")
    void updateTransaction(String message) throws JsonProcessingException {

        JSONObject updatedTransaction = objectMapper.readValue(message,JSONObject.class);

        String transactionId = (String) updatedTransaction.get("transactionId");
        String transactionStatus = (String) updatedTransaction.get("transactionStatus");

        TransactionEntity transactionEntity = transactionRepository.findByTransactionId(transactionId);

        if(transactionStatus.equalsIgnoreCase("success")) {
            transactionEntity.setStatus(TransactionStatus.SUCCESS);
        }
        else if(transactionStatus.equalsIgnoreCase("rejected")) {
            transactionEntity.setStatus(TransactionStatus.REJECTED);
        }
        else{
            transactionEntity.setStatus(TransactionStatus.FAILED);
        }

        transactionRepository.save(transactionEntity);

        // we will call notification service to send emails to both the users...
        callNotificationService(transactionEntity);


    }

    private void callNotificationService(TransactionEntity transactionEntity) {

        // fetch the emails of both the users from USER-SERVICES...
        String fromUser = transactionEntity.getFromUser();

        URI uri = URI.create("http://localhost:8072/user/get?userName="+fromUser);

        HttpEntity httpEntity = new HttpEntity(new HttpHeaders());

        // here we are calling the API of get user present inside UserController...
        // When we use restTemplate.exchange( 1, 2, 3, 4) -> it returns ResponseEntity<>...
        // but we want object instead of ResponseEntity therefore we use -> .getBody(); ...
        // now it is having UserEntity as JSONObject ...
        JSONObject senderObject = restTemplate.exchange(uri, HttpMethod.GET,httpEntity,JSONObject.class).getBody();

        String senderEmail = (String)senderObject.get("email");
        String senderName = (String) senderObject.get("name");


        // *************** Doing same for RECEIVER *****************
        String toUser = transactionEntity.getToUser();

        uri = URI.create("http://localhost:8072/user/get?userName="+toUser);

        JSONObject receiverObject = restTemplate.exchange(uri,HttpMethod.GET,httpEntity,JSONObject.class).getBody();

        String receiverEmail = (String)receiverObject.get("email");
        String receiverName = (String) receiverObject.get("name");

        //**********************************************************

        //==================================================================================================================

        // ================NOW WE HAVE TO SEND THE MAIL TO BOTH SENDER AND RECEIVER ==============
        // SENDER WILL ALWAYS GET A MAIL WEATHER TRANSACTION IS FAILED OR SUCCESSFUL......
        // BUT RECEIVER SHOULD ONLY GET THE MAIL WHEN TRANSACTION IS SUCCESSFUL.....

        String transactionStatus = transactionEntity.getStatus().toString();
        String transactionAmount = Integer.toString(transactionEntity.getAmount());
        String transactionId = transactionEntity.getTransactionId();


        JSONObject emailRequestForSender = new JSONObject();

        String messageForSender = String.format("Hi! %s , the transaction of amount = %s Rupees sending to %s with transactionId ( %s ) has been %s",
                                        senderName,
                                        transactionAmount,
                                        receiverName,
                                        transactionId,
                                        transactionStatus);

        emailRequestForSender.put("email",senderEmail);
        emailRequestForSender.put("message",messageForSender);

        String stringMessage = emailRequestForSender.toJSONString();

        kafkaTemplate.send("send_email",stringMessage);


        // NOW CHECK IF TRANSACTION IS SUCCESSFUL THEN SEND THE MAIL TO RECEIVER ALSO...

        // if status != success then return ...
        if(!transactionStatus.equalsIgnoreCase("success")){
            return;
        }

        // else if status =  success then send message to receiver also...
        JSONObject emailRequestForReceiver = new JSONObject();

        String messageForReceiver = String.format("Hi! %s , you have received rupees %s/- from %s ; transactionId is %s",
                                                receiverName,
                                                transactionAmount,
                                                senderName,
                                                transactionId);

        emailRequestForReceiver.put("email",receiverEmail);
        emailRequestForReceiver.put("message",messageForReceiver);

        stringMessage = emailRequestForReceiver.toJSONString();

        kafkaTemplate.send("send_email",stringMessage);

    }
}
