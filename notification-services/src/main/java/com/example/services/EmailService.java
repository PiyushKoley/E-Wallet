package com.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    SimpleMailMessage simpleMailMessage;

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics="send_email",groupId = "email_group")
    void sendEmail(String message) throws JsonProcessingException {

        // DECODE THE KAFKA MESSAGE ...
        // KAFKA MESSAGE -> EMAIL ID AND MESSAGE TO SEND TO EMAIL ID...
        JSONObject emailRequest = objectMapper.readValue(message,JSONObject.class);

        String email = (String) emailRequest.get("email");
        String sendMessage = (String) emailRequest.get("message");

        simpleMailMessage.setTo(email);
        simpleMailMessage.setText(sendMessage);
        simpleMailMessage.setFrom("piyushkoley.pk@gmail.com");
        simpleMailMessage.setSubject("Transaction Message");

        javaMailSender.send(simpleMailMessage);
    }

}
