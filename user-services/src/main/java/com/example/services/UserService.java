package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    void createUser(UserRequestDto userRequestDto) {

        UserEntity userEntity = UserConvertor.convertDtoToEntity(userRequestDto);
        userRepository.save(userEntity);
    }

    UserEntity getUserByUserName(String userName) throws Exception{


        UserEntity userEntity = userRepository.findByUserName(userName);

        if(userEntity == null) {
            throw new Exception("user not found in dataBase");
        }
        return userEntity;
    }
}
