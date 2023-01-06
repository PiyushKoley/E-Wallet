package com.example.services;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfiguration {

    @Bean // get this from online, from spring website ....
    JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        // Tushar sir created this email Id in class for demo...
        mailSender.setUsername("backendacciojob@gmail.com"); // from this mailId anyone will get emails...
        mailSender.setPassword("Accio1234.");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    SimpleMailMessage getSimpleMailMessage() {
        return new SimpleMailMessage();
    }


    // *********** ONLY FOR KAFKA LISTENER **************************************
    @Bean
    Properties getKafkaListenerProperties() {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"email_group");

        return properties;
    }

    @Bean
    ConsumerFactory<String,String> getConsumerFactory() {
        return new DefaultKafkaConsumerFactory(getKafkaListenerProperties());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String,String> getKafkaListener() {
        ConcurrentKafkaListenerContainerFactory<String,String> CKLCF = new ConcurrentKafkaListenerContainerFactory<>();

        CKLCF.setConsumerFactory(getConsumerFactory());

        return CKLCF;
    }

}
