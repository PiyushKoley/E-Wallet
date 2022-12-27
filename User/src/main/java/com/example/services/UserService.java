package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    void addUser(UserRequestDto userRequestDto) {

        UserEntity userEntity = UserConvertor.convertDtoToEntity(userRequestDto);
        userRepository.save(userEntity);
    }
}
