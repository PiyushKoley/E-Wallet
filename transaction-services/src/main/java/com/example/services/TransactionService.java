package com.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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
        else{
            transactionEntity.setStatus(TransactionStatus.FAILED);
        }

        transactionRepository.save(transactionEntity);

    }
}
