package com.example.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    private String userName;
    private String name;
    private String email;
    private String mobileNo;
}
