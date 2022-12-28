package com.example.services;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer>{

    UserEntity findByUserName(String userName);


}
