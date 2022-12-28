package com.example.services;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConvertor {

    public static UserEntity convertDtoToEntity(UserRequestDto userRequestDto) {
        UserEntity userEntity = UserEntity.builder()
                                    .userName(userRequestDto.getUserName())
                                    .email(userRequestDto.getEmail())
                                    .age(userRequestDto.getAge())
                                    .mobileNo(userRequestDto.getMobileNo())
                                    .name(userRequestDto.getName())
                                    .build();

        return userEntity;
    }

}
