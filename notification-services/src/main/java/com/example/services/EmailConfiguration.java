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
