package com.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    private final String REDIS_PREFIX_KEY = "user::";

    private final String CREATE_WALLET_TOPIC = "create_wallet"; //this is the name of the topic in kafka....

    void createUser(UserRequestDto userRequestDto) {

        UserEntity userEntity = UserConvertor.convertDtoToEntity(userRequestDto);

        userEntity = userRepository.save(userEntity);
        //first save it to mysql then save it to redis ... if it fails to save to sql then it will not get saved to redis..

        //save it to redis cache...
        saveToRedis(userEntity);

        // when ever we create a user we will send request to wallet to create a wallet also, through kafka...
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userName",userEntity.getUserName());
        jsonObject.put("name",userEntity.getName());


        /*
            ******** THIS WILL BE THE jsonObject same as WalletRequestDto just like we use to send through POSTMAN *********
            {
                "userName" : "here we will have userName of the user",
                "name" : "name of the user"
            }
        */

        String message = jsonObject.toJSONString(); //this is string now but it carries whole object in it ....

        //**************** here in kafkaTemplate 1-> topic, 2-> partition(key), 3-> message *****************
        //********** 1->topic, 2-> message ************
        kafkaTemplate.send(CREATE_WALLET_TOPIC,message);

        // topic and partition we have to create manually through CMD only, here we are just sending message to topic ....
    }

    private void saveToRedis(UserEntity userEntity) {

        Map map = objectMapper.convertValue(userEntity,Map.class);

        String key = REDIS_PREFIX_KEY + userEntity.getUserName();
        redisTemplate.opsForHash().putAll(key, map);

        redisTemplate.expire(key, Duration.ofHours(12));
    }

    UserEntity getUserByUserName(String userName) throws Exception{

        // trying to get the use from redis (database)....
        String key = REDIS_PREFIX_KEY + userName;
        Map map = redisTemplate.opsForHash().entries(key);

        if(map == null || map.size()==0) { // if redis does not contains the key and value then first get it from SQL
                                            // and save it to redis also ...

            UserEntity userEntity = userRepository.findByUserName(userName); // getting from mysql ..

            if(userEntity != null) {
                saveToRedis(userEntity);  // and saving to redis also...
            }
            else{
                throw new Exception("user not found in dataBase");
            }
            return userEntity;
        }

        //if found in the redis cache...
        UserEntity userEntity = objectMapper.convertValue(map,UserEntity.class);


        return userEntity;
    }
}
