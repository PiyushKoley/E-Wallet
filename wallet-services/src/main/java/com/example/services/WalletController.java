package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    WalletService walletService;

//    @PostMapping("/create-wallet")
//    public void createWallet(@RequestParam("/userName") String userName) {
//
//        walletService.createWallet(userName);
//    }


}
