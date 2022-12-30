package com.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Properties;

@Configuration
public class UserConfig {

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


    //****************** FOR KAFKA TEMPLATE ****************
    // UserEntity is only work as a producer only and not as consumer....

    @Bean
    Properties kafkaProperties() {

        Properties properties = new Properties();

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");

        return properties;
    }

    ProducerFactory<String,String> getProducerFactory() {
        return new DefaultKafkaProducerFactory(kafkaProperties());
    }

    @Bean
    KafkaTemplate<String,String> kafkaTemplate() {

        return new KafkaTemplate<>(getProducerFactory());
    }


}
