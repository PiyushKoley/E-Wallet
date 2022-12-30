package com.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.Properties;

@Configuration
public class WalletConfig {

    @Bean
    LettuceConnectionFactory getConnectionFactory() {
        RedisStandaloneConfiguration RSC = new RedisStandaloneConfiguration();
        LettuceConnectionFactory LCF = new LettuceConnectionFactory(RSC);
        return LCF;
    }

    @Bean
    RedisTemplate<String,Object> getRedisTemplate() {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();

        RedisSerializer<String> stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);

        JdkSerializationRedisSerializer JSRS = new JdkSerializationRedisSerializer();
        redisTemplate.setValueSerializer(JSRS);
        redisTemplate.setHashValueSerializer(JSRS);

        redisTemplate.setConnectionFactory(getConnectionFactory());

        return redisTemplate;
    }

    @Bean
    ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    //*********************** below for kafka ************************

    @Bean
    Properties getKafkaProperties() {

        Properties properties = new Properties();

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "friends_group"); // this is the group name of consumers/listeners....

        return properties;
    }

    // ================== THIS IS FOR PRODUCER ==================
    @Bean
    ProducerFactory<String,String> getProducerFactory() {
         return new DefaultKafkaProducerFactory(getKafkaProperties());
    }

    @Bean // ********* this we will use in service layer for PRODUCER ********
    KafkaTemplate<String,String> getKafkaTemplate() {
        return new KafkaTemplate<>(getProducerFactory());
    }

    //========================== THIS IS FOR CONSUMER/ LISTENER ==============
    @Bean
    ConsumerFactory<String,String> getConsumerFactory() {
        return new DefaultKafkaConsumerFactory(getKafkaProperties());
    }
    // this is only for consumers bcz they have to listen simultaneous...so this property needed..

    @Bean // ***** this we will use in service layer for CONSUMER/LISTENER ********
    ConcurrentKafkaListenerContainerFactory<String,String> getConcurrentKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String,String> CKLCF = new ConcurrentKafkaListenerContainerFactory<>();
        CKLCF.setConsumerFactory(getConsumerFactory());

        return CKLCF;
    }

}
