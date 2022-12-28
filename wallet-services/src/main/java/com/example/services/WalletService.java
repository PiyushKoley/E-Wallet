package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    //****************  CREATEWALLET WILL BE CALLED THROUGH KAFKA **********
    void createWallet(String userName) {

        WalletEntity walletEntity = WalletEntity.builder()
                                    .amount(0)
                                    .userName(userName)
                                    .build();

        walletRepository.save(walletEntity);
    }

    WalletEntity incrementWallet(String userName, int amount) {

        WalletEntity oldWallet = walletRepository.findByUserName(userName);

        int newAmount = oldWallet.getAmount() + amount;

        oldWallet.setAmount(newAmount);

        WalletEntity newWallet = walletRepository.save(oldWallet); // ***** we are saving it again so it will update the changed fields only****


        // Method 2nd by custom SQL query...****** check walletRepository *******
        // long rowsAffected = walletRepository.updateWallet(userName, amount);

        return newWallet;
    }

    WalletEntity decrementWallet(String userName, int amount) {

        WalletEntity oldWallet = walletRepository.findByUserName(userName);

        int newAmount = oldWallet.getAmount() - amount;

        oldWallet.setAmount(newAmount);

        WalletEntity newWallet = walletRepository.save(oldWallet);

        return newWallet;
    }

}
