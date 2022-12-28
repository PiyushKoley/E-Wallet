package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public void createUser(@RequestBody() UserRequestDto userRequestDto) {

        userService.createUser(userRequestDto);
    }

    @GetMapping("/get")
    public UserEntity getUser(@RequestParam("userName") String userName) throws Exception {

        return userService.getUserByUserName(userName);
    }

}
