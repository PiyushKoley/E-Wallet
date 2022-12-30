package com.example.services;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.Properties;

@Configuration
public class TransactionConfig {

    @Bean
    Properties getKafkaProperties() {
        Properties properties = new Properties();

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "Transactions_consumer_group");

        return properties;

    }

    @Bean
    ProducerFactory<String,String> getProducerFactory() {
        return new DefaultKafkaProducerFactory(getKafkaProperties());
    }

    @Bean
    KafkaTemplate<String,String> getKafkaTemplate() {
        return new KafkaTemplate<>(getProducerFactory());
    }

    @Bean
    ConsumerFactory<String,String> getConsumerFactory() {
        return new DefaultKafkaConsumerFactory(getKafkaProperties());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String,String> getConcurrentKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String,String> CKLCF = new ConcurrentKafkaListenerContainerFactory<>();
        CKLCF.setConsumerFactory(getConsumerFactory());

        return CKLCF;
    }

}
